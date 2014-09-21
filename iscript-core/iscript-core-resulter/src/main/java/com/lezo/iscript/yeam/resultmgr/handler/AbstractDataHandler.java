package com.lezo.iscript.yeam.resultmgr.handler;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;

public abstract class AbstractDataHandler implements DataHandler {
	private static Logger logger = LoggerFactory.getLogger(AbstractDataHandler.class);

	@Override
	public void handle(String type, String data) {
		long start = System.currentTimeMillis();
		JSONObject dataObject = null;
		try {
			dataObject = new JSONObject(data);
			doVerify(type, dataObject);
			beforeHandle(type, dataObject);
			doHanlde(type, dataObject);
			afterHandle(type, dataObject, null);
		} catch (Exception e) {
			afterHandle(type, dataObject, e);
		} finally {
			finalCall(type, dataObject, start);
		}
	}

	protected abstract void doHanlde(String type, JSONObject dataObject) throws Exception;

	protected void doVerify(String type, JSONObject dataObject) {

	}

	protected void beforeHandle(String type, JSONObject dataObject) {
	}

	protected void afterHandle(String type, JSONObject dataObject, Throwable ex) {
		if (ex != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(this.getClass().getSimpleName());
			sb.append("].type:");
			sb.append(type);
			sb.append(",cause:");
			logger.warn(sb.toString(), ex);
		}
	}

	protected void finalCall(String type, JSONObject dataObject, long start) {
		long cost = System.currentTimeMillis() - start;
		if (cost >= 1000) {
			JSONObject argsObject = JSONUtils.get(dataObject, "args");
			String argsString = argsObject == null ? "" : argsObject.toString();
			logger.warn("type:{},args:{},cost:{}", type, argsString, cost);
		}
	}
}
