package com.lezo.iscript.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.junit.Test;

public class MPWeixinTest {

	private static final String CHARSET_NAME = "utf-8";

	@Test
	public void test() throws Exception {
		String username = "xw0327@sina.com";
		String password = "29ee76e2d715e23e2c547f3e9015ac1e";//t.md5(e.password.substr(0, 16)),
		DefaultHttpClient client = HttpBase.createHttpClient();
		String html = "";
		HttpPost post = new HttpPost("https://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN");
		HttpEntity postEntity = getPostEntity(username, password);
		post.setEntity(postEntity);
		post.addHeader("Referer", "https://mp.weixin.qq.com/");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		html = HttpBase.getContent(client, post);
		JSONObject rsObject = new JSONObject(html);
		if (rsObject.getInt("ErrCode") != 0) {
			System.err.println(html);
			return;
		}
		String loginUrl = rsObject.getString("ErrMsg");
		loginUrl = "https://mp.weixin.qq.com" + loginUrl;
		for (Cookie ck : client.getCookieStore().getCookies()) {
			System.out.println(ck);
		}
		HttpGet get = new HttpGet(loginUrl);
		get.addHeader("Referer", "https://mp.weixin.qq.com/");
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
