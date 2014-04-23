package com.lezo.rest.jos;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class JosApi {

	@Test
	public void testJosToken() throws Exception {
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "1503e8459a4b4592a281334c311e6ced";
		String appUrl = "http://www.lezomao.com";
		String code = "mr1Sxs";
		StringBuilder sb = new StringBuilder();
		sb.append("https://auth.360buy.com/oauth/token?grant_type=authorization_code");
		sb.append("&client_id=" + appKey);
		sb.append("&client_secret=" + appSecret);
		sb.append("&scope=read&redirect_uri=" + appUrl);
		sb.append("&code=" + code);
		sb.append("&state=web");

		DefaultHttpClient client = new DefaultHttpClient();
		HttpProtocolParams.setUseExpectContinue(client.getParams(), false);

		HttpPost post = new HttpPost(sb.toString());
		post.setHeader("Accept-Charset", "utf-8");
		HttpResponse respone = client.execute(post);
		HttpEntity entity = respone.getEntity();
		System.out.println(EntityUtils.toString(entity));
	}
}
