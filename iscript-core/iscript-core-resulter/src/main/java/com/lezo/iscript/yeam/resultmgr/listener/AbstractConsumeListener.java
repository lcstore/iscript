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
		JSONObject gObject = doVerify(type, data);
		if (!isAccept(type, gObject)) {
			return;
		}
	}

	protected JSONObject doVerify(String type, String data) {
		JSONObject gObject = JSONUtils.getJSONObject(data);
		if (gObject == null) {
			logger.warn("type:{},can not create dataObject,data:{}", type, data);
		}
		JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
		if (rsObject == null) {
			logger.warn("type:{},can not create rsObject,data:{}", type, data);
		}
		JSONObject tObject = JSONUtils.getJSONObject(rsObject, "target");
		if (tObject == null) {
			logger.warn("type:{},can not found tObject,data:{}", type, data);
		}
		return gObject;
	}

	protected abstract DataHandler getHandler();

	protected boolean isAccept(String type, JSONObject gObject) {
		return true;
	}
}
