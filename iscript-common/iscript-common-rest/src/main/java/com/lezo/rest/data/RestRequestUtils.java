package com.lezo.rest.data;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

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
		if (ret.getResponse() != null) {
			try {
				ret.getResponse().setEntity(new BufferedHttpEntity(ret.getResponse().getEntity()));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				EntityUtils.consumeQuietly(ret.getResponse().getEntity());
			}
		}
		return ret;
	}

	public static RestRespone doRequest(HttpClient client, HttpRequestBase request) {
		return doRequest(client, request, 5);
	}

}
