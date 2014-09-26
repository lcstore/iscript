package com.lezo.iscript.yeam.resultmgr.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.ObjectUtils;
import com.lezo.iscript.yeam.resultmgr.writer.BeanWriterManager;

public class BeanCopyDataHandler extends AbstractDataHandler {
	private static Logger logger = LoggerFactory.getLogger(BeanCopyDataHandler.class);

	/**
	 * data struct: {"target":[],"data":{},"nexts":[]}
	 */
	@Override
	protected void doHanlde(String type, JSONObject gObject) throws Exception {

		JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
		JSONArray tArray = JSONUtils.get(rsObject, "target");
		if (tArray == null) {
			logger.warn("can not found target.type:{},data:{}", type, gObject);
			return;
		}
		int len = tArray.length();
		for (int i = 0; i < len; i++) {
			try {
				saveObject(type, rsObject, tArray.getString(i));
			} catch (Exception e) {
				String msg = String.format("type:%s,class:%,cause:", type, tArray.getString(i));
				logger.warn(msg, e);
			}
		}
	}

	private void saveObject(String type, JSONObject rsObject, String clsName) throws Exception {
		Class<?> dtoClass = getDtoClass(clsName);
		JSONObject dataObject = JSONUtils.getJSONObject(rsObject, "data");
		if (dataObject == null) {
			return;
		}
		Object dtoObject = ObjectUtils.newObject(dtoClass);
		ObjectWriter<Object> writer = BeanWriterManager.getInstance().getWriter(dtoObject.getClass().getSimpleName());
		if (writer == null) {
			logger.warn("type:{},can not found writer:{}", type, dtoObject.getClass().getSimpleName());
			return;
		}
		ObjectUtils.copyObject(dataObject, dtoObject);
		List<Object> dataList = new ArrayList<Object>(1);
		dataList.add(dtoObject);
		writer.write(dataList);
	}

	private Class<?> getDtoClass(String name) throws ClassNotFoundException {
		return Thread.currentThread().getContextClassLoader().loadClass(name);
	}

}
