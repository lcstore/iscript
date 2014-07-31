package com.lezo.iscript.yeam.server.event.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	protected void doHandle(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
		offerProxys(hObject, event);
	}

	private void offerProxys(JSONObject hObject, RequestEvent event) {
		long start = System.currentTimeMillis();
		Integer active = JSONUtils.getInteger(hObject, "proxyactive");
		int remain = MAX_PROXY_PER_CLIENT - active;

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
			logger.warn("proxy error:" + eObject);
		}
		List<ProxyWritable> offerProxyList = new ArrayList<ProxyWritable>(remain);
		String clientName = JSONUtils.getString(hObject, "name");
		ProxyDetectService proxyDetectService = SpringBeanUtils.getBean(ProxyDetectService.class);
		synchronized (OFFER_LOCK) {
			// turn Error Proxy to RetryStatus
			List<Long> idList = new ArrayList<Long>(proxyIdSet);
			proxyDetectService.batchUpdateProxyStatus(idList, ProxyDetectDto.STATUS_RETRY);

			List<String> domainList = new ArrayList<String>(domainSet);
			List<ProxyDetectDto> dtoList = proxyDetectService.getUnionProxyDetectDtoFromDomain(domainList,
					ProxyDetectDto.STATUS_USABLE, 1);
			List<ProxyDetectDto> offerDetectDtos = new ArrayList<ProxyDetectDto>(remain);
			for (ProxyDetectDto dto : dtoList) {
				if (offerProxyList.size() < remain) {
					addProxyWritable(offerProxyList, dto);
					dto.setUpdateTime(new Date());
					dto.setDetector(clientName);
					dto.setStatus(ProxyDetectDto.STATUS_WORK);
					offerDetectDtos.add(dto);
				}
			}
			proxyDetectService.batchUpdateProxyDetectDtos(offerDetectDtos);
			int limit = remain - offerProxyList.size();
			if (limit > 0) {
				List<ProxyDetectDto> newDtoList = proxyDetectService.getProxyDetectDtosFromId(0L, limit,
						ProxyDetectDto.STATUS_USABLE);
				offerDetectDtos.clear();
				for (ProxyDetectDto dto : newDtoList) {
					addProxyWritable(offerProxyList, dto);
					dto.setUpdateTime(new Date());
					dto.setDetector(clientName);
					dto.setStatus(ProxyDetectDto.STATUS_WORK);
					offerDetectDtos.add(dto);
				}
				proxyDetectService.batchUpdateProxyDetectDtos(offerDetectDtos);
			}
		}
		sendProxys(event, clientName, offerProxyList, start);
		if (offerProxyList.size() < remain) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("offer proxy[%d] for client:%s,but except[%d],cost:%s", offerProxyList.size(),
					clientName, remain, cost);
			logger.warn(msg);
		}
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

	private void addProxyWritable(List<ProxyWritable> proxyList, ProxyDetectDto dto) {
		if (dto == null) {
			return;
		}
		ProxyWritable pWritable = new ProxyWritable();
		pWritable.setId(dto.getId());
		pWritable.setIp(dto.getIp());
		pWritable.setPort(dto.getPort());
		proxyList.add(pWritable);
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
		Integer active = JSONUtils.getInteger(hObject, "proxyactive");
		return active < MAX_PROXY_PER_CLIENT;
	}

}
