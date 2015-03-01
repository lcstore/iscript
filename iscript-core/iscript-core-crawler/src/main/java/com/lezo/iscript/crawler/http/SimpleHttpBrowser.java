package com.lezo.iscript.crawler.http;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class SimpleHttpBrowser {
	private DefaultHttpClient httpClient;
	private HttpContext context;
	private AtomicInteger maxExecuteCount = new AtomicInteger();
	private AtomicInteger executeCount = new AtomicInteger();

	public SimpleHttpBrowser() {
		DefaultHttpClient client = createHttpClient();
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

	private DefaultHttpClient createHttpClient() {
		ClientConnectionManager conman = createClientConnManager();
		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		client.setHttpRequestRetryHandler(new SimpleHttpRequestRetryHandler());
		client.addRequestInterceptor(new ContextResetHttpRequestInterceptor(this));
		client.addRequestInterceptor(new GzipHttpRequestInterceptor());
		client.addResponseInterceptor(new GzipHttpResponseInterceptor());
		return client;
	}

	private ClientConnectionManager createClientConnManager() {
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		supportedSchemes.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		supportedSchemes.register(new Scheme("https", 443, PlainSocketFactory.getSocketFactory()));
		ThreadSafeClientConnManager tsconnectionManager = new ThreadSafeClientConnManager(supportedSchemes);
		tsconnectionManager.setMaxTotal(HttpParamsConstant.MAX_TOTAL_CONNECTIONS);
		return tsconnectionManager;
	}

	private HttpParams createHttpParams() {
		HttpParams _params = new BasicHttpParams();
		HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(_params, HttpParamsConstant.DEFAULT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(_params, true);
		// config for user agent
		String userAgent = UserAgentManager.getRadomUserAgent();
		HttpProtocolParams.setUserAgent(_params, userAgent);
		HttpClientParams.setCookiePolicy(_params, CookiePolicy.BROWSER_COMPATIBILITY);
		// set timeout
		HttpConnectionParams.setConnectionTimeout(_params, HttpParamsConstant.DEFAULT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(_params, HttpParamsConstant.DEFAULT_TIMEOUT);
		return _params;
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
