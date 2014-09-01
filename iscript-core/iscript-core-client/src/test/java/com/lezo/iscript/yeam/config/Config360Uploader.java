package com.lezo.iscript.yeam.config;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class Config360Uploader implements ConfigParser {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		// list files
		listFiles(client);
		// JSONObject rsObject = getResultObject(task, client);
		// return rsObject.toString();
		return "";
	}

	private void listFiles(DefaultHttpClient client) throws Exception {
		HttpPost post = new HttpPost("http://c2.yunpan.360.cn/file/list");
		post.addHeader("Referer", "http://c2.yunpan.360.cn/my/index/#%2Fgod%2F");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
		paramPairs.add(new BasicNameValuePair("field", "file_name"));
		paramPairs.add(new BasicNameValuePair("order", "asc"));
		paramPairs.add(new BasicNameValuePair("page_size", "300"));
		paramPairs.add(new BasicNameValuePair("type", "2"));
		paramPairs.add(new BasicNameValuePair("ajax", "1"));
		paramPairs.add(new BasicNameValuePair("t", "0.04545558168590996"));
		paramPairs.add(new BasicNameValuePair("page", "0"));
		paramPairs.add(new BasicNameValuePair("path", "/god/"));
		post.setEntity(new UrlEncodedFormEntity(paramPairs, "UTF-8"));

		BasicClientCookie cookie =null;
		String domain = ".360.cn";
//		cookie =new BasicClientCookie("__guid", "3537848.3117976479681835500.1409331501292.5127");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("__huid", "10PS4LJlGLU+jYRmO84H+1wrwb2rrCt2JCri6kOcwDIe8=");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("__utma", "148900148.1999809836.1409332950.1409332950.1409332950.1");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("__utmz", "148900148.1409332950.1.1.utmcsr=yunpan.360.cn|utmccn=(referral)|utmcmd=referral|utmcct=/");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("gwd_unique_id", "101.69.198.89-1407939249481252372");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
		cookie =new BasicClientCookie("Q", "u=ypfgber&n=ypfgber&le=oTAmqT9lMFH0ZQRlAv5wo20=&m=ZGH5WGWOWGWOWGWOWGWOWGWOAQDk&qid=74604949&im=1_t01cbd1f3cb13b17b81&src=pcw_cloud&t=1");
		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
		cookie =new BasicClientCookie("T", "s=dc3442f93fdc60edb4cd2475c4b9824e&t=1409587013&lm=&lf=4&sk=12fcc9227a0c14dc0bae381e40f61bf6&mt=1409587013&rc=&v=2.0&a=0");
		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
		domain = ".c2.yunpan.360.cn";
		cookie =new BasicClientCookie("count", "5");
		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
		
		domain = ".yunpan.360.cn";
//		cookie =new BasicClientCookie("count", "4");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("i360loginName", "lcstore");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("is_allow_visit", "1");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("test0.970672112158471test", "test");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("test0.8378171287063823test", "test");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("test0.8225734908069097test", "test");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("test0.18672912378113227test", "test");
//		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
		cookie =new BasicClientCookie("token", "1883258002.2.a23c05ed.74604949.1409587014");
		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
//		cookie =new BasicClientCookie("YUNPAN_USER", "lcstore");
//		cookie.setDomain(domain);
//		client.getCookieStore().addCookie(cookie);

		String html = HttpClientUtils.getContent(client, post);
		System.err.println("html:"+html);
	}

	private JSONObject getResultObject(TaskWritable task, DefaultHttpClient client) throws Exception {
		String postUrl = "http://news.163.com/special/0001220O/news_json.js?" + Math.random();

		HttpGet get = new HttpGet(postUrl);
		get.addHeader("Referer", "http://news.163.com/latest/");

		String html = HttpClientUtils.getContent(client, get, "gbk");
		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", html);
		task.getArgs().remove("pwd");
		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject;
	}

}
