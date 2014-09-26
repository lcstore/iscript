package com.lezo.iscript.yeam.resultmgr.listener;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.handler.BeanCopyDataHandler;
import com.lezo.iscript.yeam.resultmgr.handler.DataHandler;

public class BeanCopyListener extends AbstractConsumeListener {
	// private static Logger logger =
	// LoggerFactory.getLogger(BeanCopyListener.class);
	private DataHandler beanCopyDataHandler = SpringBeanUtils.getBean(BeanCopyDataHandler.class);

	@Override
	protected DataHandler getHandler() {
		return beanCopyDataHandler;
	}

	@Override
	protected boolean isAccept(String type, JSONObject gObject) {
		if (gObject == null) {
			return false;
		}
		JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
		if (argsObject == null) {
			return false;
		}
		String handler = JSONUtils.getString(argsObject, "handler");
		return StringUtils.isEmpty(handler);
	}

}
