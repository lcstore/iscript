package com.lezo.http;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class ContextResetHttpRequestInterceptor implements HttpRequestInterceptor {
	private SimpleHttpBrowser browser;

	public ContextResetHttpRequestInterceptor(SimpleHttpBrowser browser) {
		super();
		this.browser = browser;
	}

	public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
		if (browser.getExecuteCount().get() >= browser.getMaxExecuteCount().get()) {
			browser.refreshContext();
		} else {
			browser.getExecuteCount().incrementAndGet();
		}
	}

}
