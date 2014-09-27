package com.lezo.iscript.yeam.resultmgr.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.MethodUtils;
import com.lezo.iscript.utils.ObjectUtils;
import com.lezo.iscript.yeam.resultmgr.writer.BeanWriterManager;

@Component
public class BeanCopyDataHandler extends AbstractDataHandler {
	private static Logger logger = LoggerFactory.getLogger(BeanCopyDataHandler.class);

	/**
	 * data struct: {"target":[],"data":{},"nexts":[]}
	 */
	@Override
	protected void doHanlde(String type, JSONObject gObject) throws Exception {

		JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
		rsObject = rsObject == null ? JSONUtils.getJSONObject(gObject, "dataString") : rsObject;
		JSONArray tArray = JSONUtils.get(rsObject, "target");
		if (tArray == null) {
			logger.warn("no target.type:{},data:{}", type, gObject);
			return;
		}
		JSONObject dataObject = JSONUtils.getJSONObject(rsObject, "data");
		if (dataObject == null) {
			logger.warn("no data.type:{},data:{}", type, gObject);
			return;
		}
		JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
		int len = tArray.length();
		for (int i = 0; i < len; i++) {
			try {
				addDestObject(type, tArray.getString(i), dataObject, argsObject);
			} catch (Exception e) {
				String msg = String.format("type:%s,class:%s,cause:", type, tArray.getString(i));
				logger.warn(msg, e);
			}
		}
	}

	private void addDestObject(String type, String clsName, JSONObject dataObject, JSONObject argsObject)
			throws Exception {
		Class<?> dtoClass = getDtoClass(clsName);
		if (dataObject == null) {
			return;
		}
		Object destObject = ObjectUtils.newObject(dtoClass);
		ObjectWriter<Object> writer = BeanWriterManager.getInstance().getWriter(destObject.getClass().getSimpleName());
		if (writer == null) {
			logger.warn("type:{},can not found writer:{}", type, destObject.getClass().getSimpleName());
			return;
		}
		ObjectUtils.copyObject(dataObject, destObject);
		addProperties(destObject, dataObject, argsObject);
		List<Object> dataList = new ArrayList<Object>(1);
		dataList.add(destObject);
		writer.write(dataList);
	}

	private void addProperties(Object destObject, JSONObject dataObject, JSONObject argsObject) throws Exception {
		addShopId(destObject, dataObject, argsObject);
		Date newDate = new Date();
		addCreateTime(destObject, newDate);
		addUpdateTime(destObject, newDate);
	}

	private void addUpdateTime(Object destObject, Date newDate) throws Exception {
		String fieldName = "updateTime";
		Method readMd = MethodUtils.getReadMethod(fieldName, destObject.getClass());
		if (readMd == null) {
			return;
		}
		Object sidObject = readMd.invoke(destObject);
		if (sidObject != null) {
			return;
		}
		Method writeMd = MethodUtils.getWriteMethod(fieldName, destObject.getClass(), Date.class);
		if (writeMd == null) {
			return;
		}
		writeMd.invoke(destObject, newDate);
	}

	private void addCreateTime(Object destObject, Date newDate) throws Exception {
		String fieldName = "createTime";
		Method readMd = MethodUtils.getReadMethod(fieldName, destObject.getClass());
		if (readMd == null) {
			return;
		}
		Object sidObject = readMd.invoke(destObject);
		if (sidObject != null) {
			return;
		}
		Method writeMd = MethodUtils.getWriteMethod(fieldName, destObject.getClass(), Date.class);
		if (writeMd == null) {
			return;
		}
		writeMd.invoke(destObject, newDate);
	}

	private void addShopId(Object destObject, JSONObject dataObject, JSONObject argsObject) throws Exception {
		String fieldName = "shopId";
		Method readMd = MethodUtils.getReadMethod(fieldName, destObject.getClass());
		if (readMd == null) {
			return;
		}
		Object sidObject = readMd.invoke(destObject);
		if (sidObject != null) {
			return;
		}
		Method writeMd = MethodUtils.getWriteMethod(fieldName, destObject.getClass(), Integer.class);
		if (writeMd == null) {
			return;
		}
		Integer shopId = JSONUtils.getInteger(argsObject, "shopId");
		if (shopId != null) {
			writeMd.invoke(destObject, shopId);
			return;
		}
		Method readUrlMd = MethodUtils.getReadMethod("productUrl", destObject.getClass());
		if (readUrlMd != null) {
			String pUrl = (String) readUrlMd.invoke(destObject);
			if (pUrl != null) {
				ShopDto shopDto = ShopCacher.getInstance().getDomainShopDto(pUrl);
				if (shopDto != null) {
					writeMd.invoke(destObject, shopDto.getId());
				}
			}
		}
	}

	private Class<?> getDtoClass(String name) throws ClassNotFoundException {
		if (name.indexOf('.') < 0) {
			name = "com.lezo.iscript.service.crawler.dto." + name;
		}
		return Thread.currentThread().getContextClassLoader().loadClass(name);
	}

}
