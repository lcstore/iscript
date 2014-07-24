package com.lezo.iscript.yeam.server.event.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.future.WriteFuture;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.writable.ProxyWritable;

public class ProxyEventHandler extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(ProxyEventHandler.class);

	protected void doHandle(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
		offerProxys(hObject, event);
		handleErrorProxys(hObject);
	}

	private void offerProxys(JSONObject hObject, RequestEvent event) {
		long start = System.currentTimeMillis();
		Integer active = JSONUtils.getInteger(hObject, "proxyactive");
		int remain = 5 - active;
		List<ProxyWritable> proxyList = new ArrayList<ProxyWritable>(remain);
		ProxyDetectService proxyDetectService = SpringBeanUtils.getBean(ProxyDetectService.class);
		if (proxyList.isEmpty()) {
			return;
		}
		IoRespone ioRespone = new IoRespone();
		ioRespone.setType(IoConstant.EVENT_TYPE_PROXY);
		ioRespone.setData(proxyList);
		WriteFuture writeFuture = event.getSession().write(ioRespone);
		if (!writeFuture.awaitUninterruptibly(IoConstant.WRITE_TIMEOUT)) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("Fail to offer proxy for client:%s@%s,cost:%s", proxyList.size(),
					JSONUtils.getString(hObject, "name"), JSONUtils.getString(hObject, "mac"), cost);
			logger.warn(msg, writeFuture.getException());
		} else {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("Offer %d proxy for client:%s@%s,cost:%s", proxyList.size(),
					JSONUtils.getString(hObject, "name"), JSONUtils.getString(hObject, "mac"), cost);
			logger.info(msg);
		}
	}

	private void handleErrorProxys(JSONObject hObject) {
		Object errorObject = JSONUtils.get(hObject, "proxyerrors");
		if (errorObject == null) {
			return;
		}
		if (errorObject instanceof List) {
			@SuppressWarnings("unchecked")
			List<JSONObject> errorList = (List<JSONObject>) errorObject;
			for (JSONObject error : errorList) {
				logger.warn("error proxy." + error);
			}
		}
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
		return active < 5;
	}

}
