package com.lezo.rest.data;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

public class RestRequestUtils {
	public static RestRespone doRequest(HttpClient client, HttpRequestBase request, int maxRetryCount) {
		RestRespone ret = new RestRespone();

		for (int retries = 0; (ret.getResponse() == null) && (retries < maxRetryCount); retries++) {
			try {
				ret.setResponse(client.execute(request));
				ret.setException(null);
			} catch (Exception e) {
				ret.setException(e);
			}
			if (ret.getResponse() == null) {
				try {
					Thread.sleep(1000 * (retries + 1));
				} catch (InterruptedException e) {
					ret.setException(e);
				}
			}
		}
		return ret;
	}

	public static RestRespone doRequest(HttpClient client, HttpRequestBase request) {
		return doRequest(client, request, 5);
	}

}
