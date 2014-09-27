package com.lezo.iscript.yeam.resultmgr.listener;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.handler.DataHandler;

public abstract class AbstractConsumeListener implements ConsumeListener {
	private static Logger logger = LoggerFactory.getLogger(AbstractConsumeListener.class);

	@Override
	public void doConsume(String type, String data) {
		try {
			JSONObject gObject = doVerify(type, data);
			if (!isAccept(type, gObject)) {
				return;
			}
			getHandler().handle(type, gObject);
		} catch (Exception ex) {

		}

	}

	protected JSONObject doVerify(String type, String data) {
		JSONObject gObject = JSONUtils.getJSONObject(data);
		if (gObject == null) {
			logger.warn("no gObject,type:{},data:{}", type, data);
			return null;
		}
		JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
		rsObject = rsObject == null ? JSONUtils.getJSONObject(gObject, "dataString") : rsObject;
		if (rsObject == null) {
			logger.warn("no rsObject,type:{},data:{}", type, data);
			return null;
		}
		return gObject;
	}

	protected abstract DataHandler getHandler();

	protected boolean isAccept(String type, JSONObject gObject) {
		return true;
	}
}
