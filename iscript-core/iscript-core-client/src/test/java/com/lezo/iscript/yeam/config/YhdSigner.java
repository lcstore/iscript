package com.lezo.iscript.yeam.config;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
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

import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class YhdSigner implements ConfigParser {

	@Override
	public String getName() {
		return "yhd-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String username = (String) task.get("user");
		String password = (String) task.get("pwd");
		Scriptable scope = EnvjsUtils.initStandardObjects(null);
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		String url = "http://www.yhd.com/";
		HttpGet get = new HttpGet(url);
		// get.addHeader("Referer", "http://bbs.zone.jd.com/forum.php");
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, get.getURI().toString());
		addCookie(client, scope, dom);
		String loginUrl = doLogin(client, username, password, dom);
		printCookies(client);

		HttpGet loginGet = new HttpGet(loginUrl);
		loginGet.addHeader("Referer",
				"http://passport.jd.com/new/login.aspx?ReturnUrl=http://boss.jd.com/redirect/goto?returnUrl=http://bbs.zone.jd.com/");
		html = HttpClientUtils.getContent(client, loginGet);
		dom = Jsoup.parse(html, loginUrl);
		JSONObject rsObject = getSignResult(task, client, dom, scope);
		return rsObject.toString();
	}

	private String doLogin(DefaultHttpClient client, String username, String password, Document dom) throws Exception {
		String html = null;
		String loginUrl = "https://passport.yhd.com/publicPassport/loginFrame.do?fromDomain=http%3A%2F%2Fwww.yhd.com&returnUrl=http%3A%2F%2Fwww.yhd.com%2F";
		// HttpGet get = new HttpGet(loginUrl);
		// String html = HttpClientUtils.getContent(client, get);
		HttpPost post = new HttpPost("https://passport.yhd.com/publicPassport/showValidate.do");
		post.addHeader("Referer", loginUrl);
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("credentials.username", username));
		HttpEntity postEntity = new UrlEncodedFormEntity(nvPairs, "utf-8");
		post.setEntity(postEntity);
//		html = HttpClientUtils.getContent(client, post);

		String postUrl = "https://passport.yhd.com/publicPassport/login.do";
		post = new HttpPost(postUrl);
		post.addHeader("Referer", loginUrl);
		postEntity = getPostEntity(username, password, dom);
		post.setEntity(postEntity);
		html = HttpClientUtils.getContent(client, post);
		String returnUrl = dom.baseUri();
		return returnUrl;
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

	private void addCookie(DefaultHttpClient client, Scriptable scope, Document dom) throws Exception {
		Context cx = EnvjsUtils.enterContext();
		StringBuilder sb = new StringBuilder();
		sb.append("location.href=\"http://www.yhd.com/\";");
		sb.append("document.domain=\"www.yhd.com\";");
		sb.append("navigator.userAgent='Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36';");
		sb.append("navigator.platform=\"Win32\";");
		sb.append("document.title=\"网上超市1号店，省力省钱省时间\";");
		sb.append("document.referrer=\"\";");
		sb.append("document.charset='UTF-8';");
		HttpGet get = new HttpGet("http://image.yihaodianimg.com/virtual-web_static/js/jq.js");
		get.addHeader("Referer",
				"http://image.yihaodianimg.com/virtual-web_static/virtual_yhd_iframe_index.html?randid=0.8163334752616863");
		String encodeJQuery = HttpClientUtils.getContent(client, get);
		sb.append(encodeJQuery);
		cx.evaluateString(scope, sb.toString(), "jQuery", 0, null);
		String baseUrl = dom.baseUri();
		sb = new StringBuilder();
		Elements elements = dom.select("body script");
		for (int i = 0; i < elements.size(); i++) {
			String script = elements.get(i).html();
			if (script.indexOf("URLPrefix") > 0 || script.indexOf("_gaq") > 0) {
				sb.append(script);
			}
		}
		sb.append("function Image(width, height){_globalImg=this;};");
		sb.append("Image.prototype = document.createElement('img');");
		get = new HttpGet("http://www.google-analytics.com/ga.js");
		get.addHeader("Referer", baseUrl);
		String jsCookieHtml = HttpClientUtils.getContent(client, get);
		sb.append(jsCookieHtml);
		sb.append("var logoUrl=_globalImg.src;");
		cx.evaluateString(scope, sb.toString(), "ga.js", 0, null);

		String logoUrl = Context.toString(ScriptableObject.getProperty(scope, "logoUrl"));
		int index = logoUrl.indexOf("?");
		String headUrl = logoUrl.substring(0, index + 1);
		String paramUrl = logoUrl.substring(index + 1);
		paramUrl = URLEncoder.encode(paramUrl, "UTF-8");
		logoUrl = headUrl + paramUrl;
		get = new HttpGet(logoUrl);
		get.addHeader("Referer", baseUrl);
		HttpClientUtils.getContent(client, get);

		get = new HttpGet("http://static.baifendian.com/api/2.0/bcore.min.js");
		get.addHeader("Referer", baseUrl);
		// String bcoreScript = HttpClientUtils.getContent(client, get);
		String bcoreScript = FileUtils.readFileToString(new File("src/test/resources/bcore.js"));
		cx.evaluateString(scope, bcoreScript, "bcore.js", 0, null);

		List<String> cookieList = new ArrayList<String>();
		cookieList.add("__utma");
		cookieList.add("__utmb");
		cookieList.add("__utmc");
		cookieList.add("__utmz");
		cookieList.add("bfd_session_id");
		cookieList.add("tma");
		cookieList.add("tmc");
		cookieList.add("tmd");
		sb.append("ilog('d.cookie:'+document.cookie);");
		for (String key : cookieList) {
			sb.append("var " + key + "Value=cookieUtils.get('" + key + "');");
		}
		sb.append("if(!__utmzValue || ''==__utmzValue){var index = __utmaValue.lastIndexOf('.');index = __utmaValue.lastIndexOf('.',index-1);var zValue = __utmaValue.substring(index+1);__utmzValue=__utmcValue+'.'+zValue+'.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)';};");
		cx.evaluateString(scope, sb.toString(), "cmd", 0, null);
		for (String key : cookieList) {
			String cookieValue = Context.toString(ScriptableObject.getProperty(scope, key + "Value"));
			BasicClientCookie cookie = new BasicClientCookie(key, cookieValue);
			cookie.setDomain(".yhd.com");
			client.getCookieStore().addCookie(cookie);
		}
		// add track cookie
		String useAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)";
		useAgent = URLEncoder.encode(useAgent, "UTF-8");
		get = new HttpGet(
				"http://tracker.yhd.com/tracker/info.do?1=1&ieVersion="
						+ useAgent
						+ "&platform=Win32&tracker_src=&infoPreviousUrl=&infoTrackerSrc=&extField6=29&cookie=&provinceId=1&cityId=null&infoPageId=6.0.0.0.0.PDlL4k&infoLinkId=&infoModuleId=&jsoncallback=jsonp"
						+ System.currentTimeMillis());
		get.addHeader("Referer", baseUrl);
		HttpClientUtils.getContent(client, get);
		printCookies(client);
		// add cookie direct.
		BasicClientCookie cookie = new BasicClientCookie("gla", "1.-10_0");
		cookie.setDomain(".yhd.com");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("provinceId", "1");
		cookie.setDomain(".yhd.com");
		client.getCookieStore().addCookie(cookie);
		cookie = new BasicClientCookie("abtest", "29");
		cookie.setDomain(".yhd.com");
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
		nvPairs.add(new BasicNameValuePair("credentials.username", username));
		nvPairs.add(new BasicNameValuePair("credentials.password", password));
		nvPairs.add(new BasicNameValuePair("isAutoLogin", "0"));
		nvPairs.add(new BasicNameValuePair("loginSource", "2"));
		nvPairs.add(new BasicNameValuePair("returnUrl", "http://www.yhd.com/"));
		nvPairs.add(new BasicNameValuePair("validCode", ""));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}
}
