package com.lezo.http;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class GzipHttpRequestInterceptor implements HttpRequestInterceptor {
	public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
		if (!request.containsHeader("Accept-Encoding")) {
			request.addHeader("Accept-Encoding", "gzip");
		}
		if (!request.containsHeader("User-Agent")) {
			String userAgent = UserAgentManager.getRadomUserAgent();
			request.addHeader("User-Agent", userAgent);
		}
	}
}
