package com.lezo.iscript.yeam.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class FDSSigner implements ConfigParser {

	@Override
	public String getName() {
		return "fds-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String username = (String) task.get("user");
		String password = (String) task.get("pwd");
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		addCookie(client);
		String postUrl = "http://www.fanduoshao.com/member/login/";
		HttpPost post = new HttpPost(postUrl);
		post.addHeader("Referer", "http://www.fanduoshao.com/member/login/");
		HttpEntity postEntity = getPostEntity(username, password);
		post.setEntity(postEntity);

		String html = HttpClientUtils.getContent(client, post);
		Document dom = Jsoup.parse(html, postUrl);
		String getUrl = "http://www.fanduoshao.com/i/order/";
		HttpGet get = new HttpGet(getUrl);
		get.addHeader("Referer", "http://www.fanduoshao.com/member/login/");
		html = HttpClientUtils.getContent(client, get);
		for (Cookie ck : client.getCookieStore().getCookies()) {
			System.out.println(ck);
		}
		JSONObject rsObject = getSignResult(task, client);
		return rsObject.toString();
	}

	private JSONObject getSignResult(TaskWritable task, DefaultHttpClient client) throws Exception {
		String postUrl = "http://www.fanduoshao.com/ajax/user_sign/";

		HttpPost post = new HttpPost(postUrl);
		post.addHeader("Referer", "http://www.fanduoshao.com/zdm/");

		String html = HttpClientUtils.getContent(client, post);
		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", html);
		task.getArgs().remove("pwd");
		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject;
	}

	private void addCookie(DefaultHttpClient client) throws Exception {
		String url = "http://www.fanduoshao.com/member/login/";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://www.fanduoshao.com/");
		HttpClientUtils.getContent(client, get);
		client.getCookieStore().addCookie(
				new BasicClientCookie("Hm_lpvt_04cc16722724f467d345b6e9da3d9a1f", "1400136027"));
		client.getCookieStore().addCookie(
				new BasicClientCookie("Hm_lvt_04cc16722724f467d345b6e9da3d9a1f", "1400128262,1400135992"));
	}

	private HttpEntity getPostEntity(String username, String password) throws Exception {
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("email", username));
		nvPairs.add(new BasicNameValuePair("pwd", password));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}
}
