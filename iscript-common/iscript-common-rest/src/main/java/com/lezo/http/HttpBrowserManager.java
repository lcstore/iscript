package com.lezo.http;

import java.util.concurrent.ConcurrentHashMap;

public class HttpBrowserManager {
	private static final ConcurrentHashMap<String, SimpleHttpBrowser> browserMap = new ConcurrentHashMap<String, SimpleHttpBrowser>();

	public static SimpleHttpBrowser buildBrowser(String name) {
		SimpleHttpBrowser browser = browserMap.get(name);
		if (browser == null) {
			browser = new SimpleHttpBrowser();
			browserMap.put(name, browser);
		}
		return browser;
	}
}
