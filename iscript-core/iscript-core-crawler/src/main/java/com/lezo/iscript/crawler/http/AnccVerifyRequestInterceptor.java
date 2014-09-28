package com.lezo.iscript.crawler.http;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;

import com.lezo.iscript.crawler.main.SessionUtils;

public class AnccVerifyRequestInterceptor implements HttpRequestInterceptor {

	private static final String COOKIE_SESSION_KEY = "ASP.NET_SessionId";

	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		addSessionCookie(context);
	}

	private void addSessionCookie(HttpContext context) {
		CookieStore cookieStore = (CookieStore) context.getAttribute(ClientContext.COOKIE_STORE);
		boolean hasSessionKey = false;
		for (Cookie hasCookie : cookieStore.getCookies()) {
			if (hasCookie.getName().equals(COOKIE_SESSION_KEY)) {
				hasSessionKey = true;
			}
		}
		if (!hasSessionKey) {
			BasicClientCookie cookie = new BasicClientCookie(COOKIE_SESSION_KEY, SessionUtils.randomSession());
			cookieStore.addCookie(cookie);
		}
	}

}
