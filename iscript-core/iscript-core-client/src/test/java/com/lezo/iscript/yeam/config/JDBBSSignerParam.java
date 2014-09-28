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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class JDBBSSignerParam implements ConfigParser {

	@Override
	public String getName() {
		return "fdbbs-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String username = (String) task.get("user");
		String password = (String) task.get("pwd");
		Scriptable scope = EnvjsUtils.initStandardObjects(null);
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		addCookie(client, scope);
		doLogin(client, username, password);

		JSONObject rsObject = getSignResult(task, client);
		return rsObject.toString();
	}

	private void doLogin(DefaultHttpClient client, String username, String password) throws Exception {
		HttpGet get = new HttpGet("http://boss.jd.com/login?ReturnUrl=http://bbs.zone.jd.com/");
		get.addHeader("Referer", "http://bbs.zone.jd.com/forum.php");
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, get.getURI().toString());
		String uuid = dom.select("#uuid[value]").first().attr("value");
		BasicClientCookie cookie = new BasicClientCookie("track", uuid);
		cookie.setDomain(".jd.com");
		client.getCookieStore().addCookie(cookie);
		
		printCookies(client);
		
		String postUrl = "http://passport.jd.com/uc/loginService?uuid=" + uuid
				+ "&ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/&r=" + Math.random();
		HttpPost post = new HttpPost(postUrl);
		post.addHeader("Referer", "http://www.fanduoshao.com/member/login/");
		HttpEntity postEntity = getPostEntity(username, password, dom);
		post.setEntity(postEntity);
		html = HttpClientUtils.getContent(client, post);
		System.out.println(html);
		
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
		String url = "http://bbs.zone.jd.com/forum.php";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://zone.jd.com/index/portal.htm");
		HttpClientUtils.getContent(client, get);
		BasicClientCookie cookie = new BasicClientCookie("eQCe_4518_sendmail", "1");
		cookie.setDomain(".jd.com");
		client.getCookieStore().addCookie(cookie);
		url ="http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/";
		get = new HttpGet(url);
		get.addHeader("Referer", "http://bbs.zone.jd.com/forum.php");
		HttpClientUtils.getContent(client, get);
		
		Context cx = EnvjsUtils.enterContext();
		StringBuilder sb = new StringBuilder();
		sb.append("location.href=\"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/\";");
		sb.append("document.domain=\"passport.jd.com\";");
		sb.append("navigator.userAgent='Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36';");
		sb.append("navigator.platform=\"Win32\";");
		sb.append("document.title=\"登录京东\";");
		sb.append("document.referrer=\"\";");
		get = new HttpGet("http://passport.jd.com/new/misc/js/jquery-1.2.6.pack.js?t=20130718");
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
		sb.append("var __jdb=cookieUtils.get('__jdb');;");
		sb.append("var __jdc=cookieUtils.get('__jdc');;");
		// sb.append("var __jdu=cookieUtils.get('__jdu');;");
		sb.append("var __jdv=cookieUtils.get('__jdv');;");
		cx.evaluateString(scope, sb.toString(), "cmd", 0, null);
        List<String> cookieList = new ArrayList<String>();
        cookieList.add("__jda");
        cookieList.add("__jdb");
        cookieList.add("__jdc");
        cookieList.add("__jdv");
        for(String key :cookieList){
        	String cookieValue = Context.toString(ScriptableObject.getProperty(scope, key));
        	cookie = new BasicClientCookie(key, cookieValue);
    		cookie.setDomain(".jd.com");
    		client.getCookieStore().addCookie(cookie);
        }
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
		nvPairs.add(new BasicNameValuePair("email", username));
		nvPairs.add(new BasicNameValuePair("pwd", password));
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
