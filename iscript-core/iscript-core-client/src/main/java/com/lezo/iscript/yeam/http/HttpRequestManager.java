package com.lezo.iscript.yeam.http;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.http.SimpleResponseHandler;

public class HttpRequestManager {
	private static Logger logger = LoggerFactory.getLogger(HttpRequestManager.class);
	private DefaultHttpClient client;
	private static final HttpRequestManager defaultManager = new HttpRequestManager();

	public HttpRequestManager() {
		client = HttpClientFactory.createHttpClient();
		SchemeRegistry schreg = client.getConnectionManager().getSchemeRegistry();
		ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(schreg, new SimpleProxySelector());
		client.setRoutePlanner(routePlanner);
	}

	public DefaultHttpClient getClient() {
		return client;
	}

	public void setClient(DefaultHttpClient client) {
		this.client = client;
	}

	public final HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException,
			ClientProtocolException {

		if (request == null) {
			throw new IllegalArgumentException("Request must not be null.");
		}
		return client.execute(request, context);
	}

	public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler,
			final HttpContext context) throws IOException, ClientProtocolException {
		return client.execute(request, responseHandler, context);
	}

	public String executeQuietly(HttpUriRequest request) {
		return executeQuietly(request, "UTF-8");
	}

	public String executeQuietly(HttpUriRequest request, String charset) {
		if (request == null) {
			throw new IllegalArgumentException("request must not be null..");
		}
		try {
			return client.execute(request, new SimpleResponseHandler(charset));
		} catch (Exception e) {
			request.abort();
			logger.warn(ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	public static HttpRequestManager getDefaultManager() {
		return defaultManager;
	}

}
