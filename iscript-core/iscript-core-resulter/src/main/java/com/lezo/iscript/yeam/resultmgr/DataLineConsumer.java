package com.lezo.iscript.yeam.resultmgr;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.handler.DataHandler;

public class DataLineConsumer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DataLineConsumer.class);
	private String type;
	private String data;

	public DataLineConsumer(String type, String data) {
		super();
		this.type = type;
		this.data = data;
	}

	@Override
	public void run() {
		JSONObject dataObject = JSONUtils.getJSONObject(data);
		if (dataObject == null) {
			logger.warn("type:{},data:{},error:can not create dataObject", type, data);
			return;
		}
		JSONObject rsObject = JSONUtils.getJSONObject(dataObject, "rs");
		if (rsObject == null) {
			logger.warn("type:{},data:{},error:can not create rsObject", type, data);
			return;
		}
		JSONObject tObject = JSONUtils.getJSONObject(rsObject, "target");
		if (tObject == null) {
			logger.warn("type:{},data:{},error:can not found tObject", type, data);
			return;
		}
		String handler = JSONUtils.getString(tObject, "handler");
		if (StringUtils.isEmpty(handler)) {
			handler = "BeanCopyDataHandler";
		}
		DataHandler dataHandler = (DataHandler) SpringBeanUtils.getBean(handler);
		dataHandler.handle(handler, dataObject);
	}

}
