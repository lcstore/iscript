package com.lezo.iscript.rest.http;

import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientManager {
	private static DefaultHttpClient defaultHttpClient;
//	private static DefaultHttpClient proxyHttpClient;

	public static DefaultHttpClient getDefaultHttpClient() {
		if (defaultHttpClient == null) {
			synchronized (HttpClientManager.class) {
				if (defaultHttpClient == null) {
					defaultHttpClient = HttpClientFactory.createHttpClient();
				}
			}
		}
		return defaultHttpClient;
	}

}
