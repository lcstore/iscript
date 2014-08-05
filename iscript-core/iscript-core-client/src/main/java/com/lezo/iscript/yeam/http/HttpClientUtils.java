package com.lezo.iscript.yeam.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {

	public static String getContent(DefaultHttpClient client, HttpUriRequest get) throws Exception {
		return getContent(client, get, "UTF-8");
	}

	public static String getContent(DefaultHttpClient client, HttpUriRequest request, String charsetName)
			throws Exception {
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(response.getEntity(), charsetName);
			}
			return null;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (request != null && !request.isAborted()) {
				request.abort();
			}
		}

	}

	public static DefaultHttpClient createHttpClient() {
		return HttpClientFactory.createHttpClient();
	}
}
