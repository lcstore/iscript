package com.lezo.iscript.utils;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONUtils {
	private static Logger log = LoggerFactory.getLogger(JSONUtils.class);

	public static JSONObject getJSONObject(Object content) {
		if (content == null) {
			return null;
		}
		JSONObject jObject = null;
		try {
			if (content instanceof JSONObject) {
				return (JSONObject) content;
			} else if (content instanceof String) {
				String sContent = content.toString();
				jObject = new JSONObject(sContent);
			} else if (content instanceof Map) {
				Map<?, ?> map = (Map<?, ?>) content;
				jObject = new JSONObject(map);
			} else if (content instanceof JSONTokener) {
				JSONTokener tokener = (JSONTokener) content;
				jObject = new JSONObject(tokener);
			} else {
				jObject = new JSONObject(content);
			}
		} catch (JSONException e) {
			log.warn("Fail to convert JSONObject..");
		}
		return jObject;
	}

	public static void put(JSONObject jObj, String key, Object value) {
		try {
			jObj.putOpt(key, value);
		} catch (JSONException e) {
			log.warn("Fail to put " + key, e);
		}
	}

	public static <T> T get(JSONObject jObj, String key) {
		if (!jObj.isNull(key)) {
			try {
				return (T) jObj.get(key);
			} catch (JSONException e) {
				log.warn("Fail to get " + key, e);
			}
		}
		return null;
	}

	public static Object getObject(JSONObject jObj, String key) {
		return get(jObj, key);
	}

	public static String getString(JSONObject jObj, String key) {
		Object jObject = get(jObj, key);
		if (jObject == null) {
			return null;
		}
		if (jObject instanceof String) {
			return (String) jObject;
		}
		return jObject.toString();
	}

	public static Integer getInteger(JSONObject jObj, String key) {
		Object jObject = get(jObj, key);
		if (jObject == null) {
			return null;
		}
		if (jObject instanceof Double) {
			Double dValue = (Double) jObject;
			return dValue.intValue();
		} else if (jObject instanceof Float) {
			Float fValue = (Float) jObject;
			return fValue.intValue();
		}
		return Integer.valueOf(jObject.toString());
	}

	public static Float getFloat(JSONObject jObj, String key) {
		Object jObject = get(jObj, key);
		if (jObject == null) {
			return null;
		}
		if (jObject instanceof Double) {
			Double dValue = (Double) jObject;
			return dValue.floatValue();
		}
		return Float.valueOf(jObject.toString());
	}

	public static Long getLong(JSONObject jObj, String key) {
		Object jObject = get(jObj, key);
		if (jObject == null) {
			return null;
		}
		if (jObject instanceof Long) {
			return (Long) jObject;
		}
		return Long.valueOf(jObject.toString());
	}

	public static JSONObject getJSONObject(JSONObject jObj, String key) {
		Object jObject = get(jObj, key);
		if (jObject == null) {
			return null;
		}
		if (jObject instanceof JSONObject) {
			return (JSONObject) jObject;
		}
		return JSONUtils.getJSONObject(jObject.toString());
	}

}
