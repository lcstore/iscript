package com.yihaodian.pis.config.session;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class URLUtils {

	public static Map<String, String> getParamMap(String url) throws Exception {
		if (url == null) {
			return new HashMap<String, String>();
		}
		return getParamMap(new URL(url));
	}

	public static Map<String, String> getParamMap(URL url) {
		if (url == null || url.getQuery() == null) {
			return new HashMap<String, String>();
		}
		String[] queryArray = url.getQuery().split("&");
		Map<String, String> paramMap = new HashMap<String, String>();
		for (String query : queryArray) {
			String[] pArray = query.split("=");
			paramMap.put(pArray[0], (pArray.length >= 2) ? pArray[1] : "");
		}
		return paramMap;
	}
}
