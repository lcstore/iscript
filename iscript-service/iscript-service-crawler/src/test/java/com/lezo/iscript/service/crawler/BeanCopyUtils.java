package com.lezo.iscript.service.crawler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.MethodUtils;
import com.lezo.iscript.utils.ObjectUtils;

public class BeanCopyUtils {
	private static Logger logger = LoggerFactory.getLogger(BeanCopyUtils.class);

	/**
	 * data struct: {"dataList":[],"nextList":[]}, args: {"target":[]}|{"target":"Class"}
	 * 
	 * @return
	 */
	public static Map<String, List<Object>> doHanlde(String type, JSONObject gObject) throws Exception {

		JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
		rsObject = rsObject == null ? JSONUtils.getJSONObject(gObject, "dataString") : rsObject;
		JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
		JSONArray tArray = getTargetArray(rsObject, argsObject);
		if (tArray == null) {
			logger.warn("no target.type:{},data:{}", type, gObject);
			return Collections.emptyMap();
		}
		Object dataObject = JSONUtils.get(rsObject, "dataList");
		dataObject = dataObject != null ? dataObject : JSONUtils.get(rsObject, "data");
		JSONArray dataArray = null;
		if (dataObject instanceof JSONArray) {
			dataArray = (JSONArray) dataObject;
		} else if (dataObject instanceof JSONObject) {
			dataArray = new JSONArray();
			dataArray.put(dataObject);
		}
		if (dataArray == null) {
			logger.warn("no data.type:{},data:{}", type, gObject);
			return Collections.emptyMap();
		}
		if (dataArray.length() < 1) {
			return Collections.emptyMap();
		}
		int len = tArray.length();
		Map<String, List<Object>> targetMap = new HashMap<String, List<Object>>();
		for (int i = 0; i < len; i++) {
			try {
				String targetName = tArray.getString(i);
				List<Object> dataList = addDestObject(type, targetName, dataArray, argsObject);
				targetMap.put(targetName, dataList);
			} catch (Exception e) {
				String msg = String.format("type:%s,class:%s,cause:", type, tArray.getString(i));
				logger.warn(msg, e);
			}
		}
		return targetMap;
	}

	private static JSONArray getTargetArray(JSONObject rsObject, JSONObject argsObject) {
		Object tObject = JSONUtils.get(argsObject, "target");
		tObject = tObject == null ? JSONUtils.get(rsObject, "target") : tObject;
		JSONArray dataArray = null;
		if (tObject instanceof JSONArray) {
			dataArray = (JSONArray) tObject;
		} else if (tObject instanceof String) {
			dataArray = new JSONArray();
			dataArray.put(tObject.toString());
		} else {
			dataArray = new JSONArray();
		}
		return dataArray;
	}

	private static List<Object> addDestObject(String type, String clsName, JSONArray dataArray, JSONObject argsObject) throws Exception {
		clsName = clsName.replace("BrandStoreDto", "BrandConfigVo");
		Class<?> dtoClass = ConfigClassUtils.getDtoClass(clsName);
		Object destObject = ObjectUtils.newObject(dtoClass);
		int len = dataArray.length();
		List<Object> dataList = new ArrayList<Object>(len);
		for (int i = 0; i < len; i++) {
			JSONObject rObject = dataArray.getJSONObject(i);
			ObjectUtils.copyObject(rObject, destObject);
			try {
				addProperties(destObject, rObject, argsObject);
				dataList.add(destObject);
			} catch (Exception e) {
				logger.warn("add data fail.cause:", e);
			}
		}
		return dataList;
	}

	private static void addProperties(Object destObject, JSONObject dataObject, JSONObject argsObject) throws Exception {
		addSiteId(destObject, dataObject, argsObject);
		addShopId(destObject, dataObject, argsObject);
		Date newDate = new Date();
		addCreateTime(destObject, newDate);
		addUpdateTime(destObject, newDate);
	}

	private static void addUpdateTime(Object destObject, Date newDate) throws Exception {
		String fieldName = "updateTime";
		Method readMd = MethodUtils.getReadMethod(fieldName, destObject.getClass());
		if (readMd == null) {
			return;
		}
		Method writeMd = MethodUtils.getWriteMethod(fieldName, destObject.getClass(), Date.class);
		if (writeMd == null) {
			return;
		}
		writeMd.invoke(destObject, newDate);
	}

	private static void addCreateTime(Object destObject, Date newDate) throws Exception {
		String fieldName = "createTime";
		Method readMd = MethodUtils.getReadMethod(fieldName, destObject.getClass());
		if (readMd == null) {
			return;
		}
		Method writeMd = MethodUtils.getWriteMethod(fieldName, destObject.getClass(), Date.class);
		if (writeMd == null) {
			return;
		}
		writeMd.invoke(destObject, newDate);
	}

	private static void addShopId(Object destObject, JSONObject dataObject, JSONObject argsObject) throws Exception {
		Integer stockNum = JSONUtils.getInteger(dataObject, "stockNum");
		if (stockNum != null && stockNum < 0) {
			return;
		}
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
		String shopUrl = JSONUtils.getString(dataObject, "shopUrl");
		String shopCode = JSONUtils.getString(dataObject, "shopCode");
		String shopName = JSONUtils.getString(dataObject, "shopName");
		if (!StringUtils.isEmpty(shopUrl) && !StringUtils.isEmpty(shopName)) {
			ShopDto shopDto = ShopCacher.getInstance().insertIfAbsent(shopName, shopUrl, shopCode);
			if (shopDto != null) {
				writeMd.invoke(destObject, shopDto.getId());
				return;
			}
		}
		String msg = String.format("can not set shopId.args:%s,data:%s", argsObject, dataObject);
		throw new IllegalAccessException(msg);
	}

	private static void addSiteId(Object destObject, JSONObject dataObject, JSONObject argsObject) throws Exception {
		String fieldName = "siteId";
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
		String productUrl = JSONUtils.getString(dataObject, "productUrl");
		if (!StringUtils.isEmpty(productUrl)) {
			ShopDto shopDto = ShopCacher.getInstance().getDomainShopDto(productUrl);
			if (shopDto != null) {
				writeMd.invoke(destObject, shopDto.getId());
				return;
			}
		}
		String msg = String.format("can not set siteId.args:%s,data:%s", argsObject, dataObject);
		throw new IllegalAccessException(msg);
	}

}
