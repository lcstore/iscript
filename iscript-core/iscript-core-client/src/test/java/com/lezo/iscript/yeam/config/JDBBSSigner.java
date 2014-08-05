package com.lezo.iscript.yeam.config;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class JDBBSSigner implements ConfigParser {

	@Override
	public String getName() {
		return "jdbbs-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String username = (String) task.get("user");
		String password = (String) task.get("pwd");
		Scriptable scope = EnvjsUtils.initStandardObjects(null);
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		String url = "http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/";
		HttpGet get = new HttpGet(url);
		// get.addHeader("Referer", "http://bbs.zone.jd.com/forum.php");
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, get.getURI().toString());
		addCookie(client, scope);
		String loginUrl = doLogin(client, username, password, dom);

		HttpGet loginGet = new HttpGet(loginUrl);
		loginGet.addHeader("Referer",
				"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/");
		html = HttpClientUtils.getContent(client, loginGet);
		dom = Jsoup.parse(html, loginUrl);
		JSONObject rsObject = getSignResult(task, client, dom, scope);
		return rsObject.toString();
	}

	private String doLogin(DefaultHttpClient client, String username, String password, Document dom) throws Exception {
		String postUuid = dom.select("#uuid[value]").first().attr("value");
		String postUrl = "http://passport.jd.com/uc/loginService?uuid=" + postUuid
				+ "&ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/&r=" + Math.random();
		HttpPost post = new HttpPost(postUrl);
		post.addHeader("Referer",
				"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/");
		HttpEntity postEntity = getPostEntity(username, password, dom);
		post.setEntity(postEntity);
		String html = HttpClientUtils.getContent(client, post);
		JSONObject jObject = new JSONObject(html.substring(1, html.length() - 1));
		if (!jObject.has("success")) {
			throw new InterruptedException(jObject.toString());
		}
		String loginUrl = JSONUtils.getString(jObject, "success");
		return loginUrl;
	}

	private JSONObject getSignResult(TaskWritable task, DefaultHttpClient client, Document dom, Scriptable scope)
			throws Exception {
		Elements scriptEls = dom.select("head > script[type]");
		StringBuilder sb = new StringBuilder();
		for (Element element : scriptEls) {
			String script = element.html();
			sb.append(script);
		}
		HttpGet get = new HttpGet("http://www.google-analytics.com/ga.js");
		String html = HttpClientUtils.getContent(client, get);
		sb.append(html);
		get = new HttpGet("http://www.google-analytics.com/plugins/ga/inpage_linkid.js");
		html = HttpClientUtils.getContent(client, get);
		sb.append(html);

		List<String> cookieList = new ArrayList<String>();
		cookieList.add("__utma");
		cookieList.add("__utmb");
		cookieList.add("__utmc");
		cookieList.add("__utmz");
		sb.append("ilog(document.cookie);");
		for (String key : cookieList) {
			sb.append("var " + key + "Value=cookieUtils.get('" + key + "');");
		}
		sb.append("if(!__utmzValue || ''==__utmzValue){var index = __utmaValue.lastIndexOf('.');index = __utmaValue.lastIndexOf('.',index-1);var zValue = __utmaValue.substring(index+1);__utmzValue='1.'+zValue+'.3.utmcsr=passport.jd.com|utmccn=(referral)|utmcmd=referral|utmcct=/new/login.aspx';};");
		Context cx = EnvjsUtils.enterContext();
		cx.evaluateString(scope, sb.toString(), "cmd", 0, null);
		for (String key : cookieList) {
			String cookieValue = Context.toString(ScriptableObject.getProperty(scope, key + "Value"));
			BasicClientCookie cookie = new BasicClientCookie(key, cookieValue);
			cookie.setDomain(".bbs.zone.jd.com");
			client.getCookieStore().addCookie(cookie);
		}
		BasicClientCookie cookie = new BasicClientCookie("__utmli", "chart");
		cookie.setDomain(".bbs.zone.jd.com");
		client.getCookieStore().addCookie(cookie);
		printCookies(client);

		// visit forum home page.
		get = new HttpGet("http://bbs.zone.jd.com/plugin.php?id=dsu_paulsign:sign");
		get.addHeader("Referer", "http://bbs.zone.jd.com/forum.php");
		html = HttpClientUtils.getContent(client, get);
		dom = Jsoup.parse(html, get.getURI().toString());

		// do daily sign
		HttpPost post = new HttpPost(
				"http://bbs.zone.jd.com/plugin.php?id=dsu_paulsign:sign&operation=qiandao&infloat=1&inajax=1");
		HttpEntity entity = getSignEntity(dom);
		post.setEntity(entity);
		HttpClientUtils.getContent(client, post);

		// get sign message.
		TimeUnit.MILLISECONDS.sleep(3000);
		get = new HttpGet("http://bbs.zone.jd.com/plugin.php?id=dsu_paulsign:sign");
		get.addHeader("Referer", "http://bbs.zone.jd.com/plugin.php?id=dsu_paulsign:sign");
		html = HttpClientUtils.getContent(client, get);
		dom = Jsoup.parse(html, get.getURI().toString());
		Elements signAs = dom.select("#ct div.mn:contains(累计已签到) p");
		StringBuilder msgBuilder = new StringBuilder();
		for (Element element : signAs) {
			msgBuilder.append(element.text());
			msgBuilder.append(", ");
		}
		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", msgBuilder.toString());
		task.getArgs().remove("pwd");
		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject;
	}

	private HttpEntity getSignEntity(Document dom) throws Exception {
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		Elements hashAs = dom.select("#scbar_form input[name=formhash][value]");
		String hash = hashAs.first().attr("value]");
		nvPairs.add(new BasicNameValuePair("formhash", hash));
		nvPairs.add(new BasicNameValuePair("fastreply", "0"));
		nvPairs.add(new BasicNameValuePair("qdmode", "3"));
		nvPairs.add(new BasicNameValuePair("qdxq", "kx"));
		nvPairs.add(new BasicNameValuePair("todaysay", ""));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}

	private void addCookie(DefaultHttpClient client, Scriptable scope) throws Exception {
		Context cx = EnvjsUtils.enterContext();
		StringBuilder sb = new StringBuilder();
		sb.append("location.href=\"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/\";");
		sb.append("document.domain=\"passport.jd.com\";");
		sb.append("navigator.userAgent='Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36';");
		sb.append("navigator.platform=\"Win32\";");
		sb.append("document.title=\"登录京东\";");
		sb.append("document.referrer=\"\";");
		sb.append("function Image(width, height){_globalImg=this;};");
		sb.append("Image.prototype = document.createElement('img');");
		HttpGet get = new HttpGet("http://passport.jd.com/new/misc/js/jquery-1.2.6.pack.js?t=20130718");
		get.addHeader("Referer",
				"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/");
		String encodeJQuery = HttpClientUtils.getContent(client, get);
		String callJQuery = "var jQ =" + encodeJQuery + ";eval(jQ);";
		sb.append(callJQuery);
		get = new HttpGet("http://csc.jd.com/wl.js");
		get.addHeader("Referer",
				"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/");
		String jsCookieHtml = HttpClientUtils.getContent(client, get);
		sb.append(jsCookieHtml);
		sb.append("var __jda=cookieUtils.get('__jda');");
		sb.append("var __jdb=cookieUtils.get('__jdb');");
		sb.append("var __jdc=cookieUtils.get('__jdc');");
		// sb.append("var __jdu=cookieUtils.get('__jdu');;");
		sb.append("var __jdv=cookieUtils.get('__jdv');");
		sb.append("var logoUrl=_globalImg.src;");
		cx.evaluateString(scope, sb.toString(), "cmd", 0, null);
		List<String> cookieList = new ArrayList<String>();
		cookieList.add("__jda");
		cookieList.add("__jdb");
		cookieList.add("__jdc");
		cookieList.add("__jdv");
		for (String key : cookieList) {
			String cookieValue = Context.toString(ScriptableObject.getProperty(scope, key));
			BasicClientCookie cookie = new BasicClientCookie(key, cookieValue);
			cookie.setDomain(".jd.com");
			client.getCookieStore().addCookie(cookie);
		}
		String logoUrl = Context.toString(ScriptableObject.getProperty(scope, "logoUrl"));
		int index = logoUrl.indexOf("?");
		String headUrl = logoUrl.substring(0, index + 1);
		String paramUrl = logoUrl.substring(index + 1);
		paramUrl = URLEncoder.encode(paramUrl, "UTF-8");
		logoUrl = headUrl + paramUrl;
		get = new HttpGet(logoUrl);
		get.addHeader("Referer",
				"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/");
		HttpClientUtils.getContent(client, get);
		// add track cookie
		get = new HttpGet("http://passport.jd.com/new/misc/js/cookieTrack1.js?t=20130718");
		get.addHeader("Referer",
				"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/");
		String trackCode = HttpClientUtils.getContent(client, get);
		sb = new StringBuilder();
		sb.append(trackCode);
		sb.append("var trackId = newTrackID();");
		cx.evaluateString(scope, sb.toString(), "cmd", 0, null);

		String trackId = Context.toString(ScriptableObject.getProperty(scope, "trackId"));
		BasicClientCookie cookie = new BasicClientCookie("track", trackId);
		cookie.setDomain(".jd.com");
		client.getCookieStore().addCookie(cookie);
	}

	private void printCookies(DefaultHttpClient client) {
		List<Cookie> cookieList = client.getCookieStore().getCookies();
		if (cookieList.isEmpty()) {
			System.out.println("no cookie...");
		} else {
			for (Cookie ck : cookieList) {
				System.out.println(ck);
			}
		}

	}

	private HttpEntity getPostEntity(String username, String password, Element dom) throws Exception {
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		Elements paramAs = dom.select("#formlogin input[name][value]");
		for (Element ele : paramAs) {
			String name = ele.attr("name").trim();
			String value = ele.attr("value").trim();
			if ("loginpwd".equals(name)) {
				value = password;
			}
			nvPairs.add(new BasicNameValuePair(name, value));
		}
		nvPairs.add(new BasicNameValuePair("loginname", username));
		nvPairs.add(new BasicNameValuePair("chkRememberMe", "on"));
		nvPairs.add(new BasicNameValuePair("authcode", ""));
		nvPairs.add(new BasicNameValuePair("nloginpwd", password));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}
}
