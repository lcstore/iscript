package com.lezo.iscript.yeam.simple.utils;

import com.lezo.iscript.yeam.http.HttpRequestManager;

public class HttpUtils {
	private static final HttpRequestManager httpRequestManager = new HttpRequestManager();

	public static HttpRequestManager getDefaultHttpRequestManager() {
		return httpRequestManager;
	}
}
