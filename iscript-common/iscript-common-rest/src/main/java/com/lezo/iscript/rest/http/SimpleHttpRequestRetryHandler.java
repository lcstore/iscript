package com.lezo.iscript.rest.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

public class SimpleHttpRequestRetryHandler implements HttpRequestRetryHandler {
	private static Logger log = Logger.getLogger(SimpleHttpRequestRetryHandler.class);
	private static final int MAX_RETRY_SIZE = 5;

	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		if (executionCount >= MAX_RETRY_SIZE) {
			log.warn("Retry request failed(" + executionCount + ").", exception);
			return false;
		}
		if (exception instanceof NoHttpResponseException) {
			return true;
		}
		if (exception instanceof InterruptedIOException) {
			return true;
		}
		if (exception instanceof UnknownHostException) {
			return false;
		}
		if (exception instanceof SSLHandshakeException) {
			return false;
		}
		HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
		boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
		if (idempotent) {
			return true;
		}
		return false;
	}

}
