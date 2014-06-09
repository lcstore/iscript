package com.lezo.iscript.yeam.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.envjs.PrototypeJavaObject;
import com.lezo.iscript.envjs.dom.DocumentAdapt;
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
		Scriptable scope = EnvjsUtils.initStandardObjects(null);
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		addCookie(client, scope);
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

	private void addCookie(DefaultHttpClient client, Scriptable scope) throws Exception {
		String url = "http://www.fanduoshao.com/member/login/";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://www.fanduoshao.com/");
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, url);
		Elements scriptAs = dom.select("html body script");
		String sMark = "hm.baidu.com/hm.js";
		Context cx = EnvjsUtils.enterContext();
		for (Element element : scriptAs) {
			String curString = element.html();
			if (curString.indexOf(sMark) > 0) {
				cx.evaluateString(scope, curString, "cmd", 0, null);
				break;
			}
		}
		PrototypeJavaObject documentObject = (PrototypeJavaObject) ScriptableObject.getProperty(scope, "document");
		DocumentAdapt document = (DocumentAdapt) Context.jsToJava(documentObject, DocumentAdapt.class);
		NodeList scriptList = document.getElementsByTagName("script");
		Node firstNode = scriptList.item(0);
		String hmUrl = firstNode.getAttributes().getNamedItem("src").getNodeValue();
		hmUrl = hmUrl.indexOf("http:") < 0 ? "http:" + hmUrl : hmUrl;
		get = new HttpGet(hmUrl);
		get.addHeader("Referer", url);
		String source = HttpClientUtils.getContent(client, get);
		// init some script
		StringBuilder sb = new StringBuilder();
		sb.append("document.referrer=\"http://www.fanduoshao.com/job/\";");
		sb.append("location.href=\"http://www.fanduoshao.com/\";");
		sb.append("function Image(width, height){};");
		sb.append("Image.prototype = document.createElement('img');");
		source = sb.toString() + source;
		cx.evaluateString(scope, source, "cmd", 0, null);
		String cookie = document.getCookie();
		Pattern oReg = Pattern.compile("(Hm_lpvt_.*?)=(.*?);");
		Matcher matcher = oReg.matcher(cookie);
		if (matcher.find()) {
			String name = matcher.group(1).trim();
			String value = matcher.group(2);
			value = value == null ? "" : value.trim();
			client.getCookieStore().addCookie(new BasicClientCookie(name, value));
		}
		oReg = Pattern.compile("(Hm_lvt_.*?)=(.*?);");
		matcher = oReg.matcher(cookie);
		if (matcher.find()) {
			String name = matcher.group(1).trim();
			String value = matcher.group(2);
			value = value == null ? "" : value.trim();
			client.getCookieStore().addCookie(new BasicClientCookie(name, value));
		}
	}

	private HttpEntity getPostEntity(String username, String password) throws Exception {
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("email", username));
		nvPairs.add(new BasicNameValuePair("pwd", password));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}
}
