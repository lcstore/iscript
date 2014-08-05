package com.lezo.iscript.yeam.server.event.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.future.WriteFuture;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.writable.ProxyWritable;

public class ProxyEventHandler extends AbstractEventHandler {
	private static final int MAX_PROXY_PER_CLIENT = 5;
	private static Logger logger = LoggerFactory.getLogger(ProxyEventHandler.class);
	private static final Object OFFER_LOCK = new Object();
	private ProxyDetectService proxyDetectService = SpringBeanUtils.getBean(ProxyDetectService.class);
	private ConcurrentHashMap<String, Integer> clientProxyMap = new ConcurrentHashMap<String, Integer>();

	protected void doHandle(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
		offerProxys(hObject, event);
	}

	private void offerProxys(JSONObject hObject, RequestEvent event) {
		long start = System.currentTimeMillis();
		Integer active = JSONUtils.getInteger(hObject, "proxyactive");
		int remain = MAX_PROXY_PER_CLIENT - active;
		String clientName = JSONUtils.getString(hObject, "name");

		List<JSONObject> errorList = getErrorProxys(hObject);
		Set<String> domainSet = new HashSet<String>();
		Set<Long> proxyIdSet = new HashSet<Long>();
		for (JSONObject eObject : errorList) {
			proxyIdSet.add(JSONUtils.getLong(eObject, "id"));
			JSONArray eArray = JSONUtils.get(eObject, "errors");
			if (eArray != null) {
				for (int i = 0; i < eArray.length(); i++) {
					try {
						String sUrl = JSONUtils.getString(eArray.getJSONObject(i), "url");
						String host = URLUtils.getRootHost(sUrl);
						domainSet.add(host);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			logger.warn("client:" + clientName + ",proxy error:" + eObject);
		}
		// turn Error Proxy to RetryStatus
		List<Long> idList = new ArrayList<Long>(proxyIdSet);
		proxyDetectService.batchUpdateProxyStatus(idList, ProxyDetectDto.STATUS_RETRY);

		List<ProxyDetectDto> offerProxyList = new ArrayList<ProxyDetectDto>(remain);

		synchronized (OFFER_LOCK) {
			List<ProxyDetectDto> updateList = new ArrayList<ProxyDetectDto>(remain);
			if (domainSet.isEmpty()) {
				// first request for proxy
				List<ProxyDetectDto> lostWorkList = turnLost2RetryStatus(clientName);
				updateList.addAll(lostWorkList);
			} else {
				List<ProxyDetectDto> offerByDomains = offerDomainProxy(domainSet, remain);
				offerProxyList.addAll(offerByDomains);
			}
			int limit = remain - offerProxyList.size();
			if (limit > 0) {
				List<ProxyDetectDto> newDtoList = proxyDetectService.getProxyDetectDtosFromId(0L, limit,
						ProxyDetectDto.STATUS_USABLE);
				offerProxyList.addAll(newDtoList);
			}
			List<ProxyDetectDto> update2WorkList = turn2WorkDto(offerProxyList, clientName);
			updateList.addAll(update2WorkList);
			proxyDetectService.batchUpdateProxyDetectDtos(updateList);
		}
		List<ProxyWritable> proxyWritableList = getProxyWritables(offerProxyList);
		sendProxys(event, clientName, proxyWritableList, start);
		if (offerProxyList.size() < remain) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("offer proxy[%d] for client:%s,but except[%d],cost:%s", offerProxyList.size(),
					clientName, remain, cost);
			logger.warn(msg);
		}
		clientProxyMap.put(clientName, active + offerProxyList.size());
	}

	private List<ProxyWritable> getProxyWritables(List<ProxyDetectDto> offerProxyList) {
		List<ProxyWritable> writableList = new ArrayList<ProxyWritable>(offerProxyList.size());
		for (ProxyDetectDto dto : offerProxyList) {
			ProxyWritable pWritable = new ProxyWritable();
			pWritable.setId(dto.getId());
			pWritable.setIp(dto.getIp());
			pWritable.setPort(dto.getPort());
			writableList.add(pWritable);
		}
		return writableList;
	}

	private List<ProxyDetectDto> turn2WorkDto(List<ProxyDetectDto> offerProxyList, String clientName) {
		Date curDate = new Date();
		for (ProxyDetectDto dto : offerProxyList) {
			dto.setStatus(ProxyDetectDto.STATUS_WORK);
			dto.setDetector(clientName);
			dto.setUpdateTime(curDate);
		}
		return offerProxyList;
	}

	private List<ProxyDetectDto> offerDomainProxy(Set<String> domainSet, int remain) {
		List<String> domainList = new ArrayList<String>(domainSet);
		List<ProxyDetectDto> dtoList = proxyDetectService.getUnionProxyDetectDtoFromDomain(domainList,
				ProxyDetectDto.STATUS_USABLE, 1);
		List<ProxyDetectDto> offerProxyList = new ArrayList<ProxyDetectDto>(remain);
		for (ProxyDetectDto dto : dtoList) {
			if (offerProxyList.size() < remain) {
				offerProxyList.add(dto);
			}
		}
		return offerProxyList;
	}

	private List<ProxyDetectDto> turnLost2RetryStatus(String clientName) {
		List<ProxyDetectDto> workDtos = proxyDetectService.getProxyDetectDtosFromId(0L, Integer.MAX_VALUE,
				ProxyDetectDto.STATUS_WORK);
		List<ProxyDetectDto> workList = new ArrayList<ProxyDetectDto>();
		Date curDate = new Date();
		for (ProxyDetectDto dto : workDtos) {
			if (clientName.equals(dto.getDetector())) {
				dto.setStatus(ProxyDetectDto.STATUS_RETRY);
				dto.setUpdateTime(curDate);
				workList.add(dto);
			}
		}
		return workList;
	}

	private void sendProxys(RequestEvent event, String clientName, List<ProxyWritable> offerProxyList, long start) {
		if (offerProxyList.isEmpty()) {
			return;
		}
		IoRespone ioRespone = new IoRespone();
		ioRespone.setType(IoConstant.EVENT_TYPE_PROXY);
		ioRespone.setData(offerProxyList);
		WriteFuture writeFuture = event.getSession().write(ioRespone);
		if (!writeFuture.awaitUninterruptibly(IoConstant.WRITE_TIMEOUT)) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("Fail to offer proxy[%d] for client:%s,cost:%s", offerProxyList.size(),
					clientName, cost);
			logger.warn(msg, writeFuture.getException());
		} else {
			long cost = System.currentTimeMillis() - start;
			String msg = String
					.format("offer proxy[%d] for client:%s,cost:%s", offerProxyList.size(), clientName, cost);
			logger.info(msg);
		}
	}

	@SuppressWarnings("unchecked")
	private List<JSONObject> getErrorProxys(JSONObject hObject) {
		Object errorObject = JSONUtils.get(hObject, "proxyerrors");
		if (errorObject == null) {
			return Collections.emptyList();
		}
		if (errorObject instanceof List) {
			return (List<JSONObject>) errorObject;
		}
		return Collections.emptyList();
	}

	@Override
	protected boolean isAccept(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null) {
			return false;
		}
		JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
		if (hObject == null) {
			logger.warn("get an empty header..");
			return false;
		}
		String clientName = JSONUtils.getString(hObject, "name");
		Integer proxy = clientProxyMap.get(clientName);
		if (proxy != null && proxy >= MAX_PROXY_PER_CLIENT) {
			return false;
		}
		Integer active = JSONUtils.getInteger(hObject, "proxyactive");
		return active != null && active < MAX_PROXY_PER_CLIENT;
	}

}
