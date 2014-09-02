package com.lezo.iscript.yeam.config;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
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
		postData(client);
		return "";
	}

	private void postData(DefaultHttpClient client) throws Exception {
		HttpPost addrPost = new HttpPost("http://c2.yunpan.360.cn/upload/getuploadaddress/");
		addrPost.addHeader("Accept-Encoding", "gzip, deflate");
		addrPost.addHeader("Content-Type", "application/x-www-form-urlencoded UTF-8");
		addrPost.addHeader("Referer", "http://c2.yunpan.360.cn/my/index/");
		addrPost.setEntity(new StringEntity("ajax=1"));
		String rsString = HttpClientUtils.getContent(client, addrPost);
		JSONObject tkObject = JSONUtils.getJSONObject(rsString);
		JSONObject dObject = JSONUtils.get(tkObject, "data");
		String tkString = JSONUtils.getString(dObject, "tk");
		System.err.println("rsString:" + rsString);

		String boundary = "------WebKitFormBoundaryiW0uG4pePH8W8XuC";
		MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, boundary,
				Charset.forName("UTF-8"));
		multipartEntity.addPart("qid", new StringBody("74604949", Charset.forName("UTF-8")));
		multipartEntity.addPart("ofmt", new StringBody("json", Charset.forName("UTF-8")));
		multipartEntity.addPart("method", new StringBody("Upload.web", Charset.forName("UTF-8")));
		multipartEntity.addPart("token",
				new StringBody("1883258080.2.dc85ef61.74604949.1409669803", Charset.forName("UTF-8")));
		multipartEntity.addPart("v", new StringBody("1.0.1", Charset.forName("UTF-8")));
		multipartEntity.addPart("filename", new StringBody("en123bcore.txt", Charset.forName("UTF-8")));
		multipartEntity.addPart("path", new StringBody("/god/", Charset.forName("UTF-8")));
		multipartEntity.addPart("devtype", new StringBody("web", Charset.forName("UTF-8")));
		multipartEntity.addPart("pid", new StringBody("ajax", Charset.forName("UTF-8")));
		multipartEntity.addPart("tk", new StringBody(tkString, Charset.forName("UTF-8")));
		File file = new File("src/test/resources/bcore.js");
		multipartEntity.addPart("file", new FileBody(file, "text/plain", "UTF-8"));

		String domain = JSONUtils.getString(dObject, "up");
		HttpPost post = new HttpPost("http://" + domain + "/webupload?devtype=web");
		post.setEntity(multipartEntity);
		post.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		post.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
		post.addHeader("Referer", "http://c2.yunpan.360.cn/my/index/");
		post.addHeader("Origin", "http://c2.yunpan.360.cn");
		String html = HttpClientUtils.getContent(client, post);
		System.err.println("html:" + html);

		tkObject = JSONUtils.getJSONObject(html);
		dObject = JSONUtils.get(tkObject, "data");
		tkString = JSONUtils.getString(dObject, "tk");
		String etkString = JSONUtils.getString(dObject, "etk");
		post = new HttpPost("http://c2.yunpan.360.cn/upload/addfile/");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded UTF-8");
		post.addHeader("Referer", "http://c2.yunpan.360.cn/my/index/");
		post.setEntity(new StringEntity("tk=" + tkString + "&etk=" + etkString + "&ajax=1"));
		html = HttpClientUtils.getContent(client, post);
		System.err.println("html:" + html);

	}

	private void postData2(DefaultHttpClient client) throws Exception {
		HttpPost post = new HttpPost("http://up28.yunpan.360.cn/webupload?devtype=web");
		String sContent = "en123";
		post.setEntity(new StringEntity(sContent));
		post.addHeader("qid", "74604949");
		post.addHeader("ofmt", "json");
		post.addHeader("method", "Upload.web");
		post.addHeader("token", "1883258002.2.a23c05ed.74604949.1409587014");
		post.addHeader("v", "1.0.1");
		post.addHeader("Filename", "ena1234.txt");
		post.addHeader("filename", "ena1234.txt");
		post.addHeader("path", "/god/");
		post.addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryzIeReNv2BglSW1OW");
		String html = HttpClientUtils.getContent(client, post);
		System.err.println("html:" + html);

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

		BasicClientCookie cookie = null;
		String domain = ".360.cn";
		 cookie =new BasicClientCookie("__guid",
		 "3537848.3280060191646009000.1409669781709.4595");
		 cookie.setDomain(domain);
		 client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("__huid",
		// "10PS4LJlGLU+jYRmO84H+1wrwb2rrCt2JCri6kOcwDIe8=");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("__utma",
		// "148900148.1999809836.1409332950.1409332950.1409332950.1");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("__utmz",
		// "148900148.1409332950.1.1.utmcsr=yunpan.360.cn|utmccn=(referral)|utmcmd=referral|utmcct=/");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("gwd_unique_id",
		// "101.69.198.89-1407939249481252372");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie(
				"Q",
				"u=ypfgber&n=ypfgber&le=oTAmqT9lMFH0ZQRlAv5wo20=&m=ZGH5WGWOWGWOWGWOWGWOWGWOAQDk&qid=74604949&im=1_t01cbd1f3cb13b17b81&src=pcw_cloud&t=1");
		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie(
				"T",
				"s=9bab272c2e533699b68eb26a5fd23a1d&t=1409669803&lm=&lf=4&sk=c19752112f41f81f1a5d3f8d15f22ed8&mt=1409669803&rc=&v=2.0&a=0");
		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
		domain = ".c2.yunpan.360.cn";
		cookie = new BasicClientCookie("count", "5");
		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);

		domain = ".yunpan.360.cn";
		// cookie =new BasicClientCookie("count", "4");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("i360loginName", "lcstore");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("is_allow_visit", "1");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("test0.970672112158471test", "test");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("test0.8378171287063823test", "test");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("test0.8225734908069097test", "test");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("test0.18672912378113227test", "test");
		// cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("token", "1883258080.2.dc85ef61.74604949.1409669803");
		cookie.setDomain(domain);
		client.getCookieStore().addCookie(cookie);
		// cookie =new BasicClientCookie("YUNPAN_USER", "lcstore");
		// cookie.setDomain(domain);
		// client.getCookieStore().addCookie(cookie);

		String html = HttpClientUtils.getContent(client, post);
		System.err.println("html:" + html);
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
