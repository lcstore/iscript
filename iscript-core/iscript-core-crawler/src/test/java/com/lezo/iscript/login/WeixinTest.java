package com.lezo.iscript.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;

public class WeixinTest {
	private static final String CHARSET_NAME = "utf-8";

	@Test
	public void testPost() throws Exception {
		String username = "460737029";
		String password = "b1ec0d24edc60de092e5721dba1d810f";
		password = "lcstore59922995";
		DefaultHttpClient client = HttpBase.createHttpClient();
		String html = "";
		HttpPost post = new HttpPost("http://short.weixin.qq.com/cgi-bin/micromsg-bin/searchorrecommendbiz");
//		HttpEntity postEntity = getPostEntity(username, password);
//		post.setEntity(postEntity);
//		// post.addHeader("Referer", "http://short.weixin.qq.com/");
//		post.addHeader("Content-Type", "application/octet-stream");
//		// post.addHeader("Accept-Encoding", "gzip,deflate");
//		// post.addHeader("x-GETzip", "supported");
//		html = HttpBase.getContent(client, post, "utf-8");
//		System.out.println(html);
//		JSONObject rsObject = new JSONObject(html);
//		if (rsObject.getInt("ErrCode") != 0) {
//			System.err.println(html);
//			return;
//		}
//		String loginUrl = rsObject.getString("ErrMsg");
//		loginUrl = "https://mp.weixin.qq.com" + loginUrl;
//		for (Cookie ck : client.getCookieStore().getCookies()) {
//			System.out.println(ck);
//		}
		HttpGet get = new HttpGet("https://wx.qq.com/?&lang=zh_CN");
		
		
		get.addHeader("Referer", "https://weixin.qq.com/");
		html = HttpBase.getContent(client, get);
		System.out.println(html);
	}

	private HttpEntity getPostEntity(String username, String pwd) throws IOException {
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("f", "json"));
		nvPairs.add(new BasicNameValuePair("imgcode", ""));
		nvPairs.add(new BasicNameValuePair("pwd", pwd));
		nvPairs.add(new BasicNameValuePair("username", username));
		return new UrlEncodedFormEntity(nvPairs, CHARSET_NAME);
	}
}
