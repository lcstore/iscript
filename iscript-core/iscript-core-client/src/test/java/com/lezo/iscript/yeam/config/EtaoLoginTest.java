package com.lezo.iscript.yeam.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.script.CommonContext;
import com.lezo.iscript.crawler.utils.HttpClientUtils;
import com.lezo.iscript.envjs.EnvjsUtils;
import com.lezo.iscript.yeam.ua.LoadUaFactory;
import com.lezo.iscript.yeam.ua.LogUaFactory;
import com.lezo.iscript.yeam.ua.UABuilder;
import com.lezo.iscript.yeam.ua.UaFactory;

public class EtaoLoginTest {

	private static final String CHARSET_NAME = "utf-8";

	@Test
	public void test() throws Exception {
		String username = "ajane90";
		String password = "aj891209";
		username = "lcstore99";
		password = "taobao@9Lezo";
		String ua = "186fCJmZk4PGRVHHxtNZngkZ3k+aC52PmgTKQ==|fyJ6Zyd9OWAiY3wpa3UsaBk=|fiB4D150Q1JSSgMWB1MdRUsAQR9Uc24mJT5vcCZXeQ==|eSRiYjNhIHA2cmY0eW03e2IheDp9NHltOXpvMXBoKXo+YipofDkQ|eCVoaEARTxFZARBbDU9RSAsgZDpxXHU=|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVEQgsKQxsJBRUdAh9XVEUbUVYPHkBGXVhJSVxNDwoMRVVCb3NxaXJ9fS8zYnw6alkVVxYCI3JmP3kTExgfWFxJAAUQWRsMT1EXVwtJezE5KmVnNihuPwsBEAhCR1IXEEZGF1Z5TQ==|dCtzBEAaRB1BBBBPCAJYHwRbCUgXUQkYSQUPUhUJTgFEG1N6AQ==|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWFrPGR6JmcWOA==|cCpyBVRjQWRLTwgcG0pJTwcreSlsK2xmMWl+Im51W2U=|cylxBldgQmdITAsfGElKTAQoeipvJ2BqPWVyKG1xX2E=|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKmdtOmJ2KmhxX2E=|bTdvGEl+XHlWUhUBBldUUho2ZDRxNHB6LXVhPHBkSnQ=|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwNXB6LXVhOXVgTnA=|bzVtGkt8XntUUBcDBFVWUBg0ZjZzNnB6LXVhNXhiTHI=|bjdvGElgTXdyZC8hIVRMWQEbG0sOJWl6LHRgPXEAXwxSGUsTAV8bDD0U|aTJqHUx7WXxTVxAEA1JRVx8zYTF0NGx+IGVxQGk=|aDNrHE1kSXN2YCslJVBIXQUfH08KSxMBXxgKOxI=|azN3azRnOWI+e2k/Z3IraGgudCp0MWl+KGhiP2d6P2wyYiJ6bjxkcChwbi5hIXomYHc8emwsaXwjdylzM2t/IQg=";
		ua = "152fCJmZk4PGRVHHxtEb3EtbnA3YSd/N2EaIA==|fyJ6Zyd9OWYuaHwuaXktbx4=|fiB4D150Q1JSSgMWB1MdRUsBQB5Vcm8nJD9ucSdWeA==|eSRiYjNhIHA2dGo/eWo1dG8tdDZxNHlvOndkPHBuLXg+bipoez4X|eCVoaEASTBRVFARPCAJMFAlTEj0J|ey93eSgW|ei93eSgW|dSpyBUEbRRxABRFODQdYHQdAD0wWUBIYSAoUVBEOTRpEGlwQa1E=|dCltbTwERRRTHghDa28ydWAlcDphIW16Lml/byspazx9ImQjMWIhNmovNnMpbjQYTkQSOz8O|dy52AVART307eGohZ3UtdWshbi1yN29+LGhiPXllSwRGA1EHDVISBzYf|di52AVARTxdWEARPCB9LEwZMHFtvQg==|cSlxBlcWSBBQFABLDRlEHAJFFFABLAU=|cChwB1YXSRFRFgZNCB9FHQNIHl8EKQA=|cypyBVQVS3k4fGk3b3ggZxZJGkQPXQURTwsaRG0c|cipyBVQVSxNTHglCBBZNFQpJGlANIAk=|bTRsG0oLVWcmY3UgeG0zdQRbCFYdTxcCVRcFWHEA|bDRwbDNgPmU5fG44YHUsb28pcy1zNm56MXxoKG13KH0jfyNjaT95cy11aCsV";
		ua = "223fCJmZk4PGRVHHxtEb3EtbnA3YSd/N2EaIA==|fyJ6Zyd9OWYub3Evb3ktYRA=|fiB4D150Q1JSSgMWB1MdRUsBQB5Vcm8nJD9ucSdWeA==|eSRiYjNhIHA2dGo4dGs0dmwmfz16M3BjMndjOHRsLn48ZyJgdzIb|eCVoaEARTxNRCRxXAWJ+a2VLdQ==|ey93eSgW|ei93eSgW|dSpyBUEbRRxABRFODQdYHQdAD0wWUBIYSAoUVBEOTRpEGlwQa1E=|dCltbTwERRRTHghDa28ydWAlcDphIW16Lml/byspazx9ImQjMWIhNmovNnMpbjQYTkQSOz8O|dy93AFEQThZWFQpBBhFJEQ9NGlltQA==|di93AFEQTnw9eWY4YHUobx5BEkwHVQ0ZTQ8dLAU=|cSltcS59I3gkYXMlfWgxcnI0bjBuK3NnLGF1NXZ2NGJN";
		ScriptableObject parent = CommonContext.getCommonScriptable();
		Scriptable scope = EnvjsUtils.initStandardObjects(parent);

		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		// addTabaoToken(client);
		Document homeDoc = getLoginHomeHtml(client);

		String mLoginUrl = getNextUrl(homeDoc, "div.login-tb > script");
		HttpGet getLogin = new HttpGet(mLoginUrl);
		getLogin.addHeader("Referer", homeDoc.baseUri());
		String html = HttpClientUtils.getContent(client, getLogin);
		Document mLoginDoc = Jsoup.parse(html, mLoginUrl);

		String mPayUrl = getNextUrl(homeDoc, "div.login-pay > script");
		HttpGet mPayGet = new HttpGet(mPayUrl);
		getLogin.addHeader("Referer", homeDoc.baseUri());
		html = HttpClientUtils.getContent(client, mPayGet);
		Document mPayDoc = Jsoup.parse(html, mPayUrl);

		addUm(mLoginDoc, scope, client);
		LogUaFactory logUaFactory = new LogUaFactory();
		StringBuilder sb = new StringBuilder();
		sb.append(logUaFactory.getUaOpt());
		sb.append("window.location.href='" + mLoginDoc.baseUri() + "';");
//		logUaFactory.initUa(sb.toString(), scope);
		// TODO get
		if (needcode(client, mLoginDoc, scope, username, "")) {
			return;
		}
		System.out.println("needcode=false");
//		ua = logUaFactory.getUa("window.UA_Opt.reload();", scope);
//		System.out.println("login.ua:" + ua);
		HttpEntity postEntity = getPostEntity(mLoginDoc, username, password, ua);
		// System.out.println(EntityUtils.toString(postEntity));
		HttpPost postLogin = new HttpPost("https://login.taobao.com/member/login.jhtml");
		postLogin.addHeader("Referer",
				"http://login.etao.com/loginmid.html?redirect_url=http%3A%2F%2Fjf.etao.com%2F%3F");
		postLogin.setEntity(postEntity);
		html = HttpClientUtils.getContent(client, postLogin);
		JSONObject loginObject = new JSONObject(html);
		if (!loginObject.getBoolean("state")) {
			System.err.println(loginObject);
			return;
		}
		System.out.println(html);
		String token = loginObject.getJSONObject("data").getString("token");
		String st = getStValue(client, token);
		System.out.println("st:" + st);
		String vstUrl = getVstUrl(mLoginDoc, username) + "&st=" + st;
		String callBackUrl = getCallBackUrl(client, vstUrl);
		html = HttpClientUtils.getContent(client, new HttpGet(callBackUrl));
		printCookies(client);
		// System.out.println("html:" + html);
		LoadUaFactory loadUaFactory = new LoadUaFactory();
//		logUaFactory.initUa(logUaFactory.getUaOpt(), scope);
		String callString = "window.UA_Opt.reload();";
//		ua = loadUaFactory.getUa(callString, scope);
		System.out.println("login:" + ua);
//		ua = URLEncoder.encode(ua,CHARSET_NAME);
		ua = "111PDMMH1c9M1gRdDNMbH1ZSmp%2BWE5peV5bAwY%3D%7CPzMTAicyFAAhMRAFJzAfB0I%3D%7CPjESajApWFs3NlIUfjZNAm8uTXkkF0B4NwsVVkg%3D%7COTULGSkoHRguKhsbKi8cGCotEB0%2BJQ4UOC0LHD8tCRQ6Jg4fPT1g%7CODQBGFoodhxGKGoZWjV4QQsgbmkLDWVhHUNAHg%3D%3D%7COz4XHDVB%7COj4WHDRB%7CNTsZYClMGWAoWBFhPVIMaj5GD389TxF%2BIUYRaiBTEmM%2BUg5mODtl%7CNDgEFCZPF3wgRBloRUp0fkRNdnlPSX5%2FRkd0O0QVdCZDE3oqQhJ7J00WeCVLHHhAWmxrMUls%7CNz4bZTlHFXYhQxRvJ10XcSRBFnhLJQ%3D%3D%7CNj8aZDhGFHcgQhVuJlwWcCNBF3ZKKw%3D%3D%7CMTgdYz8JWSQ6TF8vNGZaRHZ1RUF9bUlZcHVCQ3N1SigX%7CMDgcYz5BEhojLhMYPywMHFEwYRxDPm8MWTtsDTFQ%7CMzoDCi86AwgvORsINzsCDC45FxUgIAwSICsSByUrEwcmKxcYSg%3D%3D";
		HttpGet get = new HttpGet("http://jf.etao.com/ajax/getCreditForSrp.htm?jfSource=1&ua=" + ua + "&_ksTS="
				+ System.currentTimeMillis() + "_644&callback=jsonp645");
		get.addHeader("Referer", "http://jf.etao.com/?");
		html = HttpClientUtils.getContent(client, get);
		System.out.println("jf.html:" + html);
		get = new HttpGet("http://i.etao.com/api/fmsg2.html?mpp_sub_type=3&_ksTS=1403250939875_686&jsoncallback=jsonp687");
		get.addHeader("Referer", "http://jf.etao.com/?");
		System.out.println("jf2.html:" + html);
		printCookies(client);
		// for (Cookie ck : client.getCookieStore().getCookies()) {
		// System.out.println(ck);
		// }
	}

	private void addUm(Document mLoginDoc, Scriptable scope, DefaultHttpClient client) throws Exception {
		Elements scriptElements = mLoginDoc.select("script");
		String umString = "";
		for (Element ele : scriptElements) {
			String html = ele.html();
			if (html.indexOf("um.init") > 0) {
				umString = html;
				break;
			}
		}
		umString = "var um={};um.init=function(data){umToken=data.token;};" + umString;
		Context cx = EnvjsUtils.enterContext();
		cx.evaluateString(scope, umString, "tb.um", 0, null);
		String umToken = Context.toString(ScriptableObject.getProperty(scope, "umToken"));
		String format = "https://ynuf.alipay.com/service/um.json?xv=0.8.1&xt=%s";
		String callbackFun = "_2709_" + System.currentTimeMillis();
		String umUrl = String.format(format, umToken);
		umUrl += "&xa=taobao_login&xh=&x0=0%5E%5E1%5E%5E1%5E%5E1%5E%5E1%5E%5E1%5E%5E1%5E%5E1%5E%5E5%5E%5E1%5E%5Ecolor%5E%5E-%5E%5E1920%5E%5E1080&x1=1%5E%5E1%5E%5E1%5E%5E1%5E%5E0%5E%5E0%5E%5E1%5E%5E0%5E%5E0%5E%5Ezh-CN%5E%5E-%5E%5E0%5E%5EWindows%207%5E%5EWin32&x2=Mozilla%5E%5E-%5E%5ENetscape%5E%5Edb0765c6abb638f2033d0cd1b6b04dc3%5E%5E-%5E%5E-%5E%5Ef938792336584faf1e42f64b716493ae%5E%5EGoogle%20Pepper%5E%5EPlugIn%5E%5E-%5E%5E233e1cd890026c4dcc26a162dc299823%5E%5E-%5E%5EWIN%2013%2C0%2C0%2C214&x3=1046%5E%5E1920%5E%5E500%5E%5E342%5E%5E1080%5E%5Ezh-CN%5E%5Ehttps%253A%252F%252Flogin.taobao.com%252Fmember%252Flogin.jhtml%253Fstyle%253Dminiall%2526full_redirect%253Dtrue%2526css_style%253Detao%2526default_long_login%253D1%2526from%253Detao%2526enup%253Dtrue%2526tpl_redirect_url%253Dhttp%25253A%25252F%25252Flogin.etao.com%25252Floginmid.html%25253Fredirect_url%25253Dhttp%2525253A%2525252F%2525252Fjf.etao.com%2525252F%2525253F%5E%5E-%5E%5E-%5E%5E-%5E%5E1403159549407%5E%5E480%5E%5E1920&xs=&_callback="
				+ callbackFun;
		HttpGet umGet = new HttpGet(umUrl);
		umGet.addHeader("Referer", mLoginDoc.baseUri());
		String html = HttpClientUtils.getContent(client, umGet);
		printCookies(client);
		html = "var callbackFun =function(data){umdata=data.id;};" + html.replace(callbackFun, "callbackFun");
		cx.evaluateString(scope, html, "tb.um", 0, null);
		String umData = Context.toString(ScriptableObject.getProperty(scope, "umdata"));
		BasicClientCookie cookie = new BasicClientCookie("_umdata", umData);
		cookie.setDomain(".login.taobao.com");
		cookie.setPath("/member/");
		client.getCookieStore().addCookie(cookie);
	}

	private String getCallBackUrl(DefaultHttpClient client, String vstUrl) throws Exception {
		String html = HttpClientUtils.getContent(client, new HttpGet(vstUrl));
		System.out.println("getCallBackUrl:" + html);
		html = html.replaceFirst("jsonp[0-9]+\\(", "callback\\(");
		String source = "var sCallBackUrl=''; callback=function(oback){sCallBackUrl=oback.data.url;};";
		source += html;
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		cx.evaluateString(scope, source, "cmd", 0, null);
		String sCallBackUrl = Context.toString(ScriptableObject.getProperty(scope, "sCallBackUrl"));
		Context.exit();
		return sCallBackUrl;
	}

	private String getStValue(DefaultHttpClient client, String token) throws Exception {
		HttpGet get = new HttpGet("https://passport.alipay.com/mini_apply_st.js?site=0&token=" + token
				+ "&callback=vstCallback220");
		String html = HttpClientUtils.getContent(client, get);
		Pattern regex = Pattern.compile("vstCallback220.*?200\\}\\);");
		Matcher matcher = regex.matcher(html);
		if (!matcher.find()) {
			return null;
		}
		String vstScript = "var vstCallback220=function(ovst){ return ovst.data.st;};";
		String source = vstScript + "var stValue=" + matcher.group();
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		cx.evaluateString(scope, source, "cmd", 0, null);
		String st = Context.toString(ScriptableObject.getProperty(scope, "stValue"));
		Context.exit();
		return st;
	}

	private String getVstUrl(Document dom, String username) {
		Elements configAs = dom.select("#J_PluginPopup ~ script");
		String sConfig = null;
		String sMark = "window.havanaConfig";
		for (int i = 0; i < configAs.size(); i++) {
			String script = configAs.get(i).html();
			if (script.indexOf(sMark) > -1) {
				sConfig = script;
				break;
			}
		}
		String source = "var window={};" + sConfig + "var oConfig=window.havanaConfig;";
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		cx.evaluateString(scope, source, "cmd", 0, null);
		Scriptable oConfig = (Scriptable) ScriptableObject.getProperty(scope, "oConfig");
		String vstUrl = Context.toString(ScriptableObject.getProperty(oConfig, "vstUrl"));
		String vstParams = Context.toString(ScriptableObject.getProperty(oConfig, "vstParams"));
		String[] paramArr = vstParams.split(",");
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>(paramArr.length);
		for (String pid : paramArr) {
			Elements paramAs = dom.select(pid);
			if (paramAs.isEmpty()) {
				continue;
			}
			String name = paramAs.first().attr("name");
			String value = paramAs.first().attr("value");
			if (name.equals("TPL_username")) {
				value = username;
			}
			nvPairs.add(new BasicNameValuePair(name, value));
		}
		// encodeuricomponent
		try {
			String sParams = URLEncoder.encode(URLEncodedUtils.format(nvPairs, CHARSET_NAME), CHARSET_NAME);
			vstUrl = vstUrl + "?params=" + sParams;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// &_ksTS=1396191419022_91&callback=jsonp92
		Random random = new Random();
		int rand = random.nextInt(1000);
		rand = Math.abs(rand);
		String sSuffix = "&_ksTS=" + System.currentTimeMillis() + "_" + rand + "&callback=jsonp" + (++rand);
		vstUrl = vstUrl + sSuffix;
		return vstUrl;
	}

	private boolean needcode(DefaultHttpClient client, Document homeDoc, Scriptable scope, String username, String ua)
			throws Exception {
		HttpPost post = new HttpPost("https://login.taobao.com/member/request_nick_check.do?_input_charset=utf-8");
		printCookies(client);
		post.addHeader(
				"Referer",
				"https://login.taobao.com/member/login.jhtml?style=miniall&full_redirect=true&css_style=etao&default_long_login=1&from=etao&enup=true&tpl_redirect_url=http%3A%2F%2Flogin.etao.com%2Floginmid.html%3Fredirect_url%3Dhttp%253A%252F%252Fjf.etao.com%252F%253F");
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("ua", ua));
		nvPairs.add(new BasicNameValuePair("username", username));
		post.setEntity(new UrlEncodedFormEntity(nvPairs, "utf-8"));
		HttpResponse res = client.execute(post);
		String html = EntityUtils.toString(res.getEntity(), "UTF-8");
		JSONObject codeObject = new JSONObject(html);
		boolean status = codeObject.getBoolean("needcode");
		if (status) {
			System.out.println(html);
		}
		return status;
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

	private HttpEntity getPostEntity(Document dom, String username, String password, String ua)
			throws UnsupportedEncodingException {
		String tid = "";
		Elements elements = dom.select("div.submit:has(#J_SubmitStatic) input[name]");
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		for (int i = 0; i < elements.size(); i++) {
			Element ele = elements.get(i);
			String name = ele.attr("name");
			String value = "";
			if (ele.hasAttr("value")) {
				value = ele.attr("value");
			}
			if (name.equals("sr")) {
				value = "1366*768";
			} else if (name.equals("tid")) {
				value = tid;
			} else if (name.equals("osVer")) {
				value = "windows|6.1";
			} else if (name.equals("gvfdcname")) {
				value = "10";
			} else if (name.equals("callback")) {
				value = "1";
			} else if (name.equals("newlogin")) {
				value = "1";
			} else if (name.equals("pstrong")) {
				value = "2";
			} else if (name.equals("naviVer")) {
				value = "ie|8";
			}
			nvPairs.add(new BasicNameValuePair(name, value));
		}
		nvPairs.add(new BasicNameValuePair("TPL_checkcode", ""));
		nvPairs.add(new BasicNameValuePair("TPL_password", password));
		nvPairs.add(new BasicNameValuePair("TPL_username", username));
		nvPairs.add(new BasicNameValuePair("ua", ua));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}

	private void addTabaoToken(DefaultHttpClient client) throws Exception {
		String url = "http://jf.etao.com/?";
		HttpGet get = new HttpGet(url);
		String html = HttpClientUtils.getContent(client, get);
		// System.out.println(html);
	}

	private Document getLoginHomeHtml(DefaultHttpClient client) throws Exception {
		String url = "http://login.etao.com/?spm=0.0.0.0.NTyQZP&redirect_url=http%3A%2F%2Fjf.etao.com%2F%3F";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://www.etao.com/?tbpm=20140602");
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, url);
		return dom;
	}

	private HttpUriRequest getMemberLogin(DefaultHttpClient client) throws Exception {
		String url = "http://login.etao.com/?spm=0.0.0.0.NTyQZP&redirect_url=http%3A%2F%2Fjf.etao.com%2F%3F";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", "http://www.etao.com/?tbpm=20140602");
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, url);

		String mLoginUrl = getNextUrl(dom, "div.login-tb > script");
		// String mPayUrl = getNextUrl(dom,"div.login-pay > script");
		HttpGet getLogin = new HttpGet(mLoginUrl);
		getLogin.addHeader("Referer", url);
		return getLogin;
	}

	private String getNextUrl(Document dom, String query) {
		String sHtml = dom.select(query).first().html();
		String source = "var document={};document.write=function(html){};" + sHtml;
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		cx.evaluateString(scope, source, "cmd", 0, null);
		sHtml = Context.toString(ScriptableObject.getProperty(scope, "html"));
		Context.exit();
		return Jsoup.parse(sHtml).select("iframe[src]").first().attr("src");
	}
}
