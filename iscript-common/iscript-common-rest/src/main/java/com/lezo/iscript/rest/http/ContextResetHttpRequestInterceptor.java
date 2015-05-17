package com.lezo.iscript.rest.http;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

public class ContextResetHttpRequestInterceptor implements HttpRequestInterceptor {
	private static Logger log = Logger.getLogger(ContextResetHttpRequestInterceptor.class);
	private SimpleHttpBrowser browser;

	public ContextResetHttpRequestInterceptor(SimpleHttpBrowser browser) {
		super();
		this.browser = browser;
	}

	public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
		if (browser.getExecuteCount().get() >= browser.getMaxExecuteCount().get()) {
			log.info("Reset HttpContext after Request(" + browser.getExecuteCount().get() + ")");
			browser.refreshContext();
		} else {
			browser.getExecuteCount().incrementAndGet();
		}
	}

}
