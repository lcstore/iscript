package com.lezo.iscript.rest.http;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class SimpleHttpBrowser {
	private DefaultHttpClient httpClient;
	private HttpContext context;
	private AtomicInteger maxExecuteCount = new AtomicInteger();
	private AtomicInteger executeCount = new AtomicInteger();

	public SimpleHttpBrowser() {
		DefaultHttpClient client = HttpClientFactory.createHttpClient();
		this.httpClient = client;
		refreshContext();
	}

	public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler)
			throws Exception {
		try {
			HttpResponse response = httpClient.execute(request, context);
			return responseHandler.handleResponse(response);
		} catch (Exception e) {
			if (request != null && !request.isAborted()) {
				request.abort();
			}
			throw e;
		}
	}

	public DefaultHttpClient getHttpClient() {
		return httpClient;
	}

	public HttpContext getContext() {
		return context;
	}

	public AtomicInteger getMaxExecuteCount() {
		return maxExecuteCount;
	}

	public AtomicInteger getExecuteCount() {
		return executeCount;
	}

	private HttpContext createHttpContext() {
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		return localContext;
	}

	public void refreshContext() {
		this.context = createHttpContext();
		final int max = HttpParamsConstant.DEFAULT_MAX_EXEC_SIZE_VALUE;
		final int min = HttpParamsConstant.DEFAULT_MIN_EXEC_SIZE_VALUE;
		Random random = new Random();
		int maxSize = random.nextInt(max - min) + min;
		getMaxExecuteCount().set(maxSize);
		getExecuteCount().set(0);
		getHttpClient().getConnectionManager().closeExpiredConnections();
	}

}
