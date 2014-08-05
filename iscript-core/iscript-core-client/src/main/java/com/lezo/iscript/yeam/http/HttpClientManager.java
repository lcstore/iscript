package com.lezo.iscript.yeam.http;

import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;

public class HttpClientManager {
	private static DefaultHttpClient defaultHttpClient;
	private static DefaultHttpClient proxyHttpClient;

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

	public static DefaultHttpClient getProxyHttpClient() {
		if (proxyHttpClient == null) {
			synchronized (HttpClientManager.class) {
				if (proxyHttpClient == null) {
					proxyHttpClient = HttpClientFactory.createHttpClient();
					SchemeRegistry schreg = proxyHttpClient.getConnectionManager().getSchemeRegistry();
					ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(schreg,
							new SimpleProxySelector());
					proxyHttpClient.setRoutePlanner(routePlanner);
				}
			}
		}
		return proxyHttpClient;
	}
}
