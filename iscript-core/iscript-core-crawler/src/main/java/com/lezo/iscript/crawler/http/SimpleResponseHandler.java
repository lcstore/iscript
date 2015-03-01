package com.lezo.iscript.crawler.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class SimpleResponseHandler implements ResponseHandler<String> {
	private String charset;

	public SimpleResponseHandler() {
		this(HttpParamsConstant.DEFAULT_CHARSET);
	}

	public SimpleResponseHandler(String charset) {
		super();
		this.charset = charset;
	}

	@Override
	public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if (response == null) {
			return null;
		}
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return null;
		}
		String result = EntityUtils.toString(entity, charset);
		return result;
	}

}
