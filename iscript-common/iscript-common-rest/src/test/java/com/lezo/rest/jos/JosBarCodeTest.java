package com.lezo.rest.jos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lezo.rest.SignBuildable;

public class JosBarCodeTest {

	public void test() throws Exception {
		String method = "wareIdByBarCodeList";
		Map<String, Object> inMap = getRestParam(method);
		SignBuildable builder = new JosSignBuilder();
		String sign = builder.getSign(inMap);
	}

	private static Map<String, Object> getRestParam(String method) {
		Map<String, Object> inMap = getCommonMap();
		inMap.put("functionId", method);
		Map<String, Object> clientMap = new HashMap<String, Object>();
		clientMap.put("uuid", "860308028232581-acf7f34353f1");
		clientMap.put("level", 1);
		clientMap.put("isIcon", true);
		clientMap.put("isDescription", true);
		clientMap.put("client", "m");

		JSONObject paramObject = new JSONObject(clientMap);
		inMap.put("360buy_param_json", paramObject.toString());
		try {
			inMap.put("body", getParamJson());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return inMap;
	}

	private static String getParamJson() throws JSONException {
		JSONObject jObj = new JSONObject();
		jObj.put("barcode", "6937748305620");
		return jObj.toString();
	}

	private static Map<String, Object> getCommonMap() {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "1503e8459a4b4592a281334c311e6ced";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(new Date());
		Map<String, Object> inMap = new HashMap<String, Object>();
		inMap.put("access_token", accessToken);
		inMap.put("app_key", appKey);
		inMap.put("app_secret", appSecret);
		inMap.put("format", "json");
		inMap.put("timestamp", timestamp);
		inMap.put("v", "2.0");
		return inMap;
	}
}
