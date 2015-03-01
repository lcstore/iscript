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
import org.json.CookieList;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class God360Signer implements ConfigParser {

	@Override
	public String getName() {
		return "360-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String username = (String) task.get("user");
		String password = (String) task.get("pwd");
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		addCookie(client);
		String html = null;
		Context cx = EnvjsUtils.enterContext();
		Scriptable scope = EnvjsUtils.initStandardObjects(null);
		addGuid(client, cx, scope);
		BasicClientCookie cookie = new BasicClientCookie("count", "2");
		cookie.setDomain(".jifen.wan.360.cn");
		client.getCookieStore().addCookie(cookie);

		String signUrl = "http://jifen.wan.360.cn/ajax_signin_count.html?token=";
		HttpPost post = new HttpPost(signUrl);
		post.addHeader("Referer", "http://jifen.wan.360.cn/");
		html = HttpClientUtils.getContent(client, post);

		String url = "http://wan.360.cn/getuserinfo.html";
		HttpGet uGet = new HttpGet(url);
		uGet.addHeader("Referer", "http://jifen.wan.360.cn/");
		String source = HttpClientUtils.getContent(client, uGet);

		url = "http://wan.360.cn/login_pop.html?params=%7B%22defaultMethod%22%3A%22login%22%2C%22thirdLogin%22%3Atrue%2C%22src%22%3A%22360wan-top-reg%22%2C%22weakPassword%22%3Afalse%2C%22fatigue%22%3Atrue%2C%22location%22%3A%22http%253A%252F%252Fjifen.wan.360.cn%252F%22%2C%22refer%22%3A%22jifen.wan.360.cn%22%7D";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://jifen.wan.360.cn/");
		html = HttpClientUtils.getContent(client, get);
		printCookies(client);
		//
		String postUrl = "https://login.360.cn/?o=sso&m=getToken&func=QHPass.loginUtils.tokenCallback&userName="
				+ username + "&rand=0.5579256116856026&callback=QiUserJsonP1400255481580";
		HttpGet loginGet = new HttpGet(postUrl);
		loginGet.addHeader(
				"Referer",
				"http://wan.360.cn/login_pop.html?params=%7B%22defaultMethod%22%3A%22login%22%2C%22thirdLogin%22%3Atrue%2C%22src%22%3A%22360wan-top-reg%22%2C%22weakPassword%22%3Afalse%2C%22fatigue%22%3Atrue%2C%22location%22%3A%22http%253A%252F%252Fjifen.wan.360.cn%252F%22%2C%22refer%22%3A%22jifen.wan.360.cn%22%7D");
		// HttpEntity postEntity = getPostEntity(username, password);
		// post.setEntity(postEntity);
		html = HttpClientUtils.getContent(client, loginGet);
		printCookies(client);
		StringBuilder sb = new StringBuilder();
		sb.append("var doCallback=function(data){return data.token;};");
		sb.append(html.replace("QHPass.loginUtils.tokenCallback", "var token =  doCallback"));
		cx.evaluateString(scope, sb.toString(), "cmd", 0, null);
		String token = Context.toString(ScriptableObject.getProperty(scope, "token"));
		String encodePwd = "22c61f21b68effb5b3923d2598e631f0";
		String loginUrl = "https://login.360.cn/?o=sso&m=login&from=pcw_wan&rtype=data&func=QHPass.loginUtils.loginCallback&userName="
				+ username
				+ "&pwdmethod=1&password="
				+ encodePwd
				+ "&isKeepAlive=1&token="
				+ token
				+ "&captFlag=1&captId=i360&captCode=&r=1400255502029&callback=QiUserJsonP1400255481581";
		loginGet = new HttpGet(loginUrl);
		loginGet.addHeader(
				"Referer",
				"http://wan.360.cn/login_pop.html?params=%7B%22defaultMethod%22%3A%22login%22%2C%22thirdLogin%22%3Atrue%2C%22src%22%3A%22360wan-top-reg%22%2C%22weakPassword%22%3Afalse%2C%22fatigue%22%3Atrue%2C%22location%22%3A%22http%253A%252F%252Fjifen.wan.360.cn%252F%22%2C%22refer%22%3A%22jifen.wan.360.cn%22%7D");
		html = HttpClientUtils.getContent(client, loginGet);
		printCookies(client);
		// Document dom = Jsoup.parse(html, postUrl);
		// String getUrl = "http://www.fanduoshao.com/i/order/";
		// HttpGet get = new HttpGet(getUrl);
		// get.addHeader("Referer", "http://www.fanduoshao.com/member/login/");
		// html = HttpClientUtils.getContent(client, get);
		// for (Cookie ck : client.getCookieStore().getCookies()) {
		// System.out.println(ck);
		// }
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
		String url = "http://jifen.wan.360.cn/";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://jifen.wan.360.cn/");
		HttpClientUtils.getContent(client, get);

	}

	private void addGuid(DefaultHttpClient client, Context cx, Scriptable scope) throws Exception {
		String url = "http://s0.qhimg.com/monitor/;monitor/fd7e782a.js";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://jifen.wan.360.cn/");
		String html = HttpClientUtils.getContent(client, get);
		StringBuilder sb = new StringBuilder();
		sb.append("location.href='http://jifen.wan.360.cn/';");
		sb.append("document.domain='http://jifen.wan.360.cn/';");
		sb.append("var hm = document.createElement(\"script\");");
		sb.append("hm.src = \"http://s0.qhimg.com/i360/;js;pass_api_/seed,log/v3202.js\";");
		sb.append("var s = document.getElementsByTagName(\"script\")[0];");
		sb.append("s.parentNode.appendChild(hm);");
		String source = sb.toString() + html + "var guidCookie=cookieUtils.get('__guid');ilog(document.cookie);";
		cx.evaluateString(scope, source, "jscookie", 0, null);
		String value = Context.toString(ScriptableObject.getProperty(scope, "guidCookie"));
		client.getCookieStore().addCookie(new BasicClientCookie("__guid", value));
		client.getCookieStore().addCookie(new BasicClientCookie("_ga", "GA1.2.219106673.1400255410"));
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

	private HttpEntity getPostEntity(String username, String password) throws Exception {
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("email", username));
		nvPairs.add(new BasicNameValuePair("pwd", password));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}
}
