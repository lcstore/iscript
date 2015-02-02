package com.lezo.iscript.yeam.resultmgr.handler;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;

public abstract class AbstractDataHandler implements DataHandler {
	private static Logger logger = LoggerFactory.getLogger(AbstractDataHandler.class);

	@Override
	public void handle(String type, JSONObject gObject) {
		long start = System.currentTimeMillis();
		try {
			doVerify(type, gObject);
			beforeHandle(type, gObject);
			doHanlde(type, gObject);
			afterHandle(type, gObject, null);
		} catch (Exception e) {
			afterHandle(type, gObject, e);
		} finally {
			finalCall(type, gObject, start);
		}
	}

	protected abstract void doHanlde(String type, JSONObject gObject) throws Exception;

	protected void doVerify(String type, JSONObject gObject) {

	}

	protected void beforeHandle(String type, JSONObject gObject) {
	}

	protected void afterHandle(String type, JSONObject gObject, Throwable ex) {
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

	protected void finalCall(String type, JSONObject gObject, long start) {
		long cost = System.currentTimeMillis() - start;
		if (cost >= 30000) {
			JSONObject argsObject = JSONUtils.get(gObject, "args");
			String argsString = argsObject == null ? "" : argsObject.toString();
			logger.warn("cost too long time.type:{},args:{},cost:{}", type, argsString, cost);
		}
	}
}
