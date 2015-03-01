package com.lezo.iscript.yeam.client.service.impl;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class OrayTest {

	public void test() throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		String sUrl = "";
		HttpGet get = new HttpGet(sUrl);
		HttpResponse res = client.execute(get);
		System.out.println(EntityUtils.toString(res.getEntity()));
	}
}
