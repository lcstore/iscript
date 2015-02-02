package com.lezo.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class SimpleResponseHandler implements ResponseHandler<String> {

	@Override
	public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if (response == null) {
			return null;
		}
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity, HttpParamsConstant.DEFAULT_CHARSET);
		return result;
	}

}
