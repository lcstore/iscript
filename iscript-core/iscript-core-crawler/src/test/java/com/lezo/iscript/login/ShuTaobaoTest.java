package com.lezo.iscript.login;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
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
import org.mozilla.javascript.ScriptableObject;

public class ShuTaobaoTest {

	private static final String CHARSET_NAME = "UTF-8";

	@Test
	public void test() throws Exception {
		String username = "pis1002";
		String startUrl = "https://login.taobao.com/member/login.jhtml?from=taobaoindex&sub=true&redirect_url=http%3A%2F%2Fshu.taobao.com%2Flogin%2Fcallback";
		DefaultHttpClient client = HttpBase.createHttpClient();

		String html = HttpBase.getContent(client, new HttpGet("http://jf.etao.com/?"));
		HttpGet get = new HttpGet(startUrl);
		html = getContent(client, get);
		System.out.println(html);
		Document dom = Jsoup.parse(html);
		Elements elements = dom.select("#_umfp > img[src]");
		Elements umEles = dom.select("#um_to[value]");
		String payUrl = elements.first().attr("src");
		payUrl = payUrl.replace("clear.png", "um.json");
		String umto = umEles.first().attr("value");
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("x0", "0^^1^^1^^1^^1^^1^^1^^1^^5^^1^^color^^-^^1366^^768"));
		nvPairs.add(new BasicNameValuePair("x1", "1^^1^^1^^1^^0^^0^^1^^0^^0^^zh-CN^^-^^0^^Windows 7^^Win32"));
		nvPairs.add(new BasicNameValuePair(
				"x2",
				"Mozilla^^0^^Microsoft Internet Explorer^^621438846a17ede65911d27e4c2f13f3^^zh-cn^^x86^^f3912a63ef38b456e08dbc9c941ccd0b^^Adobe Windows^^ActiveX^^zh-cn^^b265c0a903b90675ad9f327f8cd65593^^zh-cn^^WIN 12,0,0,77"));
		nvPairs.add(new BasicNameValuePair(
				"x3",
				"768^^1304^^500^^342^^768^^-^^https%3A%2F%2Flogin.taobao.com%2Fmember%2Flogin.jhtml%3Fstyle%3Dminiall%26full_redirect%3Dtrue%26css_style%3Detao%26default_long_login%3D1%26from%3Detao%26enup%3Dtrue%26tpl_redirect_url%3Dhttp%253A%252F%252Flogin.etao.com%252Floginmid.html%253Fredirect_url%253Dhttp%25253A%25252F%25252Fjf.etao.com%25252F%25253F^^-^^222^^759^^1396164588299^^480^^1366"));
		nvPairs.add(new BasicNameValuePair("xv", "0.8.1"));
		nvPairs.add(new BasicNameValuePair("_callback", "_3562_" + System.currentTimeMillis()));
		nvPairs.add(new BasicNameValuePair("xh", ""));
		nvPairs.add(new BasicNameValuePair("xs", ""));
		nvPairs.add(new BasicNameValuePair("xt", umto));
		String ynufUrl = payUrl + URLEncodedUtils.format(nvPairs, "UTF-8");
		System.out.println(ynufUrl);
		html = getContent(client, new HttpGet(ynufUrl));
		System.out.println(html);
		String postUrl = "https://login.taobao.com/member/login.jhtml";
		BasicClientCookie cnaCookie = new BasicClientCookie("cna", "tJ++C3w5lFICAXBAPEt/TDwf");
		cnaCookie.setDomain(".taobao.com");
		client.getCookieStore().addCookie(cnaCookie);
		checkCode(client);
		for (Cookie ck : client.getCookieStore().getCookies()) {
			System.out.println(ck);
		}
		HttpEntity postEntity = getPostEntity(dom);
		System.out.println(EntityUtils.toString(postEntity));
		HttpPost post = new HttpPost(postUrl);
		post.setEntity(postEntity);
		post.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		// client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS,
		// false);
		html = getContent(client, post);
		System.out.println("post:" + html);
		JSONObject tokenObject = new JSONObject(html);
		tokenObject = tokenObject.getJSONObject("data");
		String token = tokenObject.getString("token");
		get = new HttpGet("https://passport.alipay.com/mini_apply_st.js?site=0&token=" + token
				+ "&callback=vstCallback220");
		html = getContent(client, get);
		Pattern regex = Pattern.compile("vstCallback220.*?200\\}\\);");
		Matcher matcher = regex.matcher(html);
		if (!matcher.find()) {
			return;
		}
		String callUrl = "http://shu.taobao.com/login/callback";
		String vstScript = "var vstCallback220=function(ovst){ return ovst.data.st;};";
		String source = vstScript + "var stValue=" + matcher.group();
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		cx.evaluateString(scope, source, "cmd", 0, null);
		String st = Context.toString(ScriptableObject.getProperty(scope, "stValue"));
		System.out.println("st:" + st);

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
		// TODO:dynamic add url
		nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("callback", "jsonp234"));
		nvPairs.add(new BasicNameValuePair("st", st));
		nvPairs.add(new BasicNameValuePair("_ksTS", System.currentTimeMillis() + "_233"));
		nvPairs.add(new BasicNameValuePair(
				"params",
				"style=default&sub=true&TPL_username="
						+ username
						+ "&loginsite=0&from_encoding=&not_duplite_str=&guf=&full_redirect=&isIgnore=&need_sign=&sign=&from=taobaoindex&TPL_redirect_url="
						+ callUrl + "&css_style=&allp="));
		String getUrl = "https://login.taobao.com/member/vst.htm?" + URLEncodedUtils.format(nvPairs, CHARSET_NAME);
		System.out.println(getUrl);
		get = new HttpGet(getUrl);
		html = getContent(client, get);
		System.out.println(html);
		BasicClientCookie bck = new BasicClientCookie("sc4", "1");
		bck.setDomain(".shu.taobao.com");
		bck.setPath("/");
		bck.setSecure(false);
		bck.setAttribute("source", "JavaScript");
		client.getCookieStore().addCookie(bck);
		client.getCookieStore().addCookie(new BasicClientCookie("_cc_", "VT5L2FSpdA=="));
		html = getContent(client, new HttpGet("http://shu.taobao.com/user/nav?_=1396158927191"));
		for (Cookie ck : client.getCookieStore().getCookies()) {
			System.out.println(ck);
		}
		System.out.println(html);
		// get = new HttpGet("http://shu.taobao.com/user/nav?_=" +
		// System.currentTimeMillis());
		// get.addHeader("Referer", "http://shu.taobao.com/");
		// html = getContent(client, get);
		// System.out.println("nav:" + html);
	}

	private Cookie getCookie(DefaultHttpClient client, String name) {
		List<Cookie> ckList = client.getCookieStore().getCookies();
		for (int i = ckList.size() - 1; i >= 0; i--) {
			Cookie ck = ckList.get(i);
			if (name.equals(ck.getName())) {
				return ck;
			}
		}
		return null;
	}

	private HttpEntity getPostEntity(Document dom) throws UnsupportedEncodingException {
		String username = "pis1002";
		String password = "pis1234";
		String tid = "XOR_1_000000000000000000000000000000_63584751370E7B737371717D";
		String ua = "186fCJmZk4PGRVHHxtNZngkZ3k+aC52PmgTKQ==|fyJ6Zyd9OWAiY3wpa3UsaBk=|fiB4D150Q1JSSgMWB1MdRUsAQR9Uc24mJT5vcCZXeQ==|eSRiYjNhIHA2cmY0eW03e2IheDp9NHltOXpvMXBoKXo+YipofDkQ|eCVoaEARTxFZARBbDU9RSAsgZDpxXHU=|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVEQgsKQxsJBRUdAh9XVEUbUVYPHkBGXVhJSVxNDwoMRVVCb3NxaXJ9fS8zYnw6alkVVxYCI3JmP3kTExgfWFxJAAUQWRsMT1EXVwtJezE5KmVnNihuPwsBEAhCR1IXEEZGF1Z5TQ==|dCtzBEAaRB1BBBBPCAJYHwRbCUgXUQkYSQUPUhUJTgFEG1N6AQ==|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWFrPGR6JmcWOA==|cCpyBVRjQWRLTwgcG0pJTwcreSlsK2xmMWl+Im51W2U=|cylxBldgQmdITAsfGElKTAQoeipvJ2BqPWVyKG1xX2E=|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKmdtOmJ2KmhxX2E=|bTdvGEl+XHlWUhUBBldUUho2ZDRxNHB6LXVhPHBkSnQ=|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwNXB6LXVhOXVgTnA=|bzVtGkt8XntUUBcDBFVWUBg0ZjZzNnB6LXVhNXhiTHI=|bjdvGElgTXdyZC8hIVRMWQEbG0sOJWl6LHRgPXEAXwxSGUsTAV8bDD0U|aTJqHUx7WXxTVxAEA1JRVx8zYTF0NGx+IGVxQGk=|aDNrHE1kSXN2YCslJVBIXQUfH08KSxMBXxgKOxI=|azN3azRnOWI+e2k/Z3IraGgudCp0MWl+KGhiP2d6P2wyYiJ6bjxkcChwbi5hIXomYHc8emwsaXwjdylzM2t/IQg=";
		// ua =
		// "093fCJmZk4PGRVHHxtNZ+ngkZ3k+aC52PmgTKQ==|fyJ6Zyd9OWEhYHIibnoiZxY=|fiB4D150Q1JSSgMWB1MdRUsAQR9Uc24mJT5vcCZXeQ==|eSRiYjNhIHA2c2U3d2YydGwudzVyNXJsMn5oMnZvKn0+YytpbVc=|eCVoaEAQThZXGw9EARNTBQ4RBBcnEx5dXl4UXwcZSHZZ|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVEQgsKQxsJBRUdAh9XVEUbUVYPHkBGXVhJSVxNDwoMRVVCb3NxaXJ9fS8zYnw6alkVVxYCI3JmP3kTExgfWFxJAAUQWRsMT1EXVwtJezE5KmVnNihuPwsBEAhCR1IXEEZGF1Z5TQ==|dCtzBEAaRB1BBBBPCAJYGQBfDUwTVQ0cTQELVhENSgVAH1d+BQ==|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWFrPGRzKm50WmQ=|cCpyBVRjQWRLTwgcG0pJTwcreSlsK2xmMWl+JWlyXGI=|cylxBldgQmdITAsfGElKTAQoeipvJ2BqPWVxLW5yXGI=|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKmdtOmJ2L2J8Umw=|bTdvGEl+XHlWUhUBBldUUho2ZDRxNHB6LXVhOn5gTnA=|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwNXB6LXVgPH9hT3E=|bzVtGkt8XntUUBcDBFVWUBg0ZjZzNnB6LXVgOX1mSHY=|bjdvGElgTXdyZC8hIVRMWQEbG0sOJWl4LnZiOHsKVQZYE0EZClwbCDkQ|aTJqHUx7WXxTVxAEA1JRVx8zYTF0NGx/KWl6S2I=|aDNrHE1kSXN2YCslJVBIXQUfH08KSxMAVhUCMxo=|azN3azRnOWI+e2k/Z3IraGgudCp0MWl+KGhiP2d6P2wyYiJ6bjxkcChwbi5hIXomYHc8emwsaXwjdylzM2t/IQg=";
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
			} else if (name.equals("gvfdcre")) {
				value = "687474703A2F2F7368752E74616F62616F2E636F6D2F";
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

	private String getContent(DefaultHttpClient client, HttpUriRequest get) throws Exception {
		get.addHeader("Referer", "http://shu.taobao.com/");
		HttpResponse res = client.execute(get);
		String html = EntityUtils.toString(res.getEntity(), "UTF-8");
		get.abort();
		return html;
	}

	private void addCookies(DefaultHttpClient client) {
		CookieStore cookieStore = client.getCookieStore();
		cookieStore.addCookie(new BasicClientCookie("_nk_", "pis1002"));
		cookieStore.addCookie(new BasicClientCookie("_cc_", "UIHiLt3xSw=="));
		cookieStore.addCookie(new BasicClientCookie("_l_g_", "Ug=="));
		cookieStore.addCookie(new BasicClientCookie("existShop", "MTM5NjA4MTc4MQ=="));
		cookieStore.addCookie(new BasicClientCookie("lgc", "pis1002"));
		cookieStore.addCookie(new BasicClientCookie("mt", "np="));
		cookieStore.addCookie(new BasicClientCookie("sc1",
				"s:NeLGMokLFNGwlbtI7oP29Qy2.HO3lnx4nC24YK7E9CRQnqLz5pBNToMZkYXMHadXPcWE"));
		cookieStore.addCookie(new BasicClientCookie("sc4", "1"));
		cookieStore
				.addCookie(new BasicClientCookie(
						"sc5",
						"208fCJmZk4PGRVHHxtIY30hYnw7bStzO20WLA==|fyJ6Zyd9OWAoanUhZnAtbRw=|fiB4D157YHtufDUqfHY4fmAxfi4QCRNRWVoPQWgZ|eSRiYjNhIHA2cmw9cGU6fmInfjx7P39vOHhtMXVvKX09ZCdldjMa|eCVoaEARTxdWFR9JBA5AXgMreFcDBhUCXlgbAwJcQQIFC1lwCw==|ey93eSgW|ei93eSgW|dSpyBUMVSxJOCxhOAwlWEAhXBUIYXgYYSQkDXh8ARQpAGl10Dw==|dCltbTwERRRWEwVOZmI/eG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU=|dy93AFEQThNSExtNAB9KBhteBU4TVhEGTQsYRR8CSBJZCU8CEE8JG0UDA0URUAkkDQ==|di52AVARTxBWEhhNDhtZGwdEF1UKSw8aSA0eRBwHRxBUYE0=|cShwB1ZEFFF6OCt0LjtuIzZzI2I8dDkvfjgybCw4ZTF6KmMvPm4jN2IgNHAmCUwMVFAVTVMPTlB+QA==|cCpyBVRGFlMSUVsMVEsWVUxiXA==|cylxBldFFVASVEsARE4TUU0IWHdD|cihwB1ZEFFETVUoBRU8SUE8NX3BE|bTVtGktZCUwKS1UcW0QRXEYGVhNCC09fClJGHlJQFE4FVR1eTxFXSBJeRQNMD1EXVUpwWQ==|bDVtGksKVGYuaXsydWo/cmgoeD1sJWFxJHxoM3EAXwxSGUsTB1AcCVF4CQ==|bzZuGUhaXVFTXlVQUBhWDnkzaCluL2J9I2J1KGt2PWkuaylpe0EZD08ZF0gZWQVGA3hC|bjZybjFiPGc7fmw6YncubW0rcS9xNGx4M35qKmlpLGMgfSFndT54bS1rdSp4JnYqZh0="));
		cookieStore.addCookie(new BasicClientCookie("sg", "226"));
		cookieStore.addCookie(new BasicClientCookie("t", "508a3954f848b60ce6f56bb0e64162c6"));
		cookieStore.addCookie(new BasicClientCookie("tg", "0"));
		cookieStore.addCookie(new BasicClientCookie("tracknick", "pis1002"));
		cookieStore
				.addCookie(new BasicClientCookie(
						"uc1",
						"lltime=1396025935&cookie14=UoLVYyKGaVq+0g==&existShop=false&cookie16=V32FPkk/xXMk5UvIbNtImtMfJQ==&cookie21=VFC/uZ9aiKCaj7Ayrcxy&tag=0&cookie15=U+GCWk/75gdr5Q=="));
		cookieStore.addCookie(new BasicClientCookie("uc3",
				"nk2=E63VLE+AfQ==&id2=UonZDmAIJB6wCg==&vt3=F8dHqR4J5zdP9/UZlhY=&lg2=W5iHLLyFOGW7aA=="));
		cookieStore.addCookie(new BasicClientCookie("unb", "1829067902"));
		cookieStore.addCookie(new BasicClientCookie("v", "0"));
	}

	private void checkCode(DefaultHttpClient client) throws Exception {
		HttpPost post = new HttpPost();
		String username = "pis1002";
		String ua = "";
		post.setURI(new URI("https://login.taobao.com/member/request_nick_check.do?_input_charset=utf-8"));
		post.addHeader(
				"Referer",
				"https://login.taobao.com/member/login.jhtml?from=taobaoindex&sub=true&redirect_url=http%3A%2F%2Fshu.taobao.com%2Flogin%2Fcallback");
		// post.addHeader("x-requested-with", "XMLHttpRequest");
		// post.addHeader("User-Agent",
		// "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
		// post.addHeader("Cookie",
		// "_umdata=C591019A2462E2B30239C727D8610B7B52B66752FC89B358491BB353D3CDD62868AACFC6A863906E872F19DC39FFF75A104A1FCD31D502F8254AEF2F16F9A6287CF7E55BE2F6B1AC; uc1=cookie14=UoLVYyKMx5Sqbw%3D%3D; v=0; cookie2=f2f0eda0a4c776d0817b793f418171ca; t=6c7c783069e7b6bb58c1609d0ae2fbb3");
		// post.addHeader("Content-Type",
		// "application/x-www-form-urlencoded; charset=UTF-8");
		// post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Cache-Control", "no-cache");
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("ua", ua));
		nvPairs.add(new BasicNameValuePair("username", username));
		post.setEntity(new UrlEncodedFormEntity(nvPairs, "utf-8"));
		HttpResponse res = client.execute(post);
		String html = EntityUtils.toString(res.getEntity(), "UTF-8");
		System.out.println(html);
	}

	private HttpEntity getPostEntity() throws Exception {
		String username = "pis1002";
		String password = "pis1234";
		String ua = "186fCJmZk4PGRVHHxtNZngkZ3k+aC52PmgTKQ==|fyJ6Zyd9OWAiY3wpa3UsaBk=|fiB4D150Q1JSSgMWB1MdRUsAQR9Uc24mJT5vcCZXeQ==|eSRiYjNhIHA2cmY0eW03e2IheDp9NHltOXpvMXBoKXo+YipofDkQ|eCVoaEARTxFZARBbDU9RSAsgZDpxXHU=|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVEQgsKQxsJBRUdAh9XVEUbUVYPHkBGXVhJSVxNDwoMRVVCb3NxaXJ9fS8zYnw6alkVVxYCI3JmP3kTExgfWFxJAAUQWRsMT1EXVwtJezE5KmVnNihuPwsBEAhCR1IXEEZGF1Z5TQ==|dCtzBEAaRB1BBBBPCAJYHwRbCUgXUQkYSQUPUhUJTgFEG1N6AQ==|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWFrPGR6JmcWOA==|cCpyBVRjQWRLTwgcG0pJTwcreSlsK2xmMWl+Im51W2U=|cylxBldgQmdITAsfGElKTAQoeipvJ2BqPWVyKG1xX2E=|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKmdtOmJ2KmhxX2E=|bTdvGEl+XHlWUhUBBldUUho2ZDRxNHB6LXVhPHBkSnQ=|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwNXB6LXVhOXVgTnA=|bzVtGkt8XntUUBcDBFVWUBg0ZjZzNnB6LXVhNXhiTHI=|bjdvGElgTXdyZC8hIVRMWQEbG0sOJWl6LHRgPXEAXwxSGUsTAV8bDD0U|aTJqHUx7WXxTVxAEA1JRVx8zYTF0NGx+IGVxQGk=|aDNrHE1kSXN2YCslJVBIXQUfH08KSxMBXxgKOxI=|azN3azRnOWI+e2k/Z3IraGgudCp0MWl+KGhiP2d6P2wyYiJ6bjxkcChwbi5hIXomYHc8emwsaXwjdylzM2t/IQg=";
		String url = "http://www.huihui.cn/activate?url=http%3A%2F%2Fwww.huihui.cn%2Flogin";
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("callback", "1"));
		nvPairs.add(new BasicNameValuePair("allp", ""));
		nvPairs.add(new BasicNameValuePair("css_style", ""));
		nvPairs.add(new BasicNameValuePair("CtrlVersion", "1,0,0,7"));
		nvPairs.add(new BasicNameValuePair("fc", "default"));
		nvPairs.add(new BasicNameValuePair("from", "taobaoindex"));
		nvPairs.add(new BasicNameValuePair("from_encoding", ""));
		nvPairs.add(new BasicNameValuePair("full_redirect", ""));
		nvPairs.add(new BasicNameValuePair("guf", ""));
		nvPairs.add(new BasicNameValuePair("gvfdcname", ""));
		nvPairs.add(new BasicNameValuePair("gvfdcre", "687474703A2F2F7368752E74616F62616F2E636F6D2F"));
		nvPairs.add(new BasicNameValuePair("isIgnore", ""));
		nvPairs.add(new BasicNameValuePair("llnick", ""));
		nvPairs.add(new BasicNameValuePair("loginsite", "0"));
		nvPairs.add(new BasicNameValuePair("loginType", "3"));
		nvPairs.add(new BasicNameValuePair("minipara", ""));
		nvPairs.add(new BasicNameValuePair("minititle", ""));
		nvPairs.add(new BasicNameValuePair("naviVer", "ie|8"));
		nvPairs.add(new BasicNameValuePair("need_check_code", ""));
		nvPairs.add(new BasicNameValuePair("need_sign", ""));
		nvPairs.add(new BasicNameValuePair("need_user_id", ""));
		nvPairs.add(new BasicNameValuePair("newlogin", "1"));
		nvPairs.add(new BasicNameValuePair("not_duplite_str", ""));
		nvPairs.add(new BasicNameValuePair("oslanguage", ""));
		nvPairs.add(new BasicNameValuePair("osVer", "windows|6.1"));
		nvPairs.add(new BasicNameValuePair("popid", ""));
		nvPairs.add(new BasicNameValuePair("poy", ""));
		nvPairs.add(new BasicNameValuePair("pstrong", "2"));
		nvPairs.add(new BasicNameValuePair("sign", ""));
		nvPairs.add(new BasicNameValuePair("sr", "1366*768"));
		nvPairs.add(new BasicNameValuePair("style", "default"));
		nvPairs.add(new BasicNameValuePair("sub", "true"));
		nvPairs.add(new BasicNameValuePair("support", "000001"));
		nvPairs.add(new BasicNameValuePair("tid", "XOR_1_000000000000000000000000000000_63584751370E7B737371717D"));
		nvPairs.add(new BasicNameValuePair("TPL_checkcode", ""));
		nvPairs.add(new BasicNameValuePair("TPL_redirect_url", "http://shu.taobao.com/login/callback"));
		nvPairs.add(new BasicNameValuePair("tid", ""));
		nvPairs.add(new BasicNameValuePair("umto", "T67f52af118ae4afe39f631851e4314dc"));
		nvPairs.add(new BasicNameValuePair("ua", ua));
		nvPairs.add(new BasicNameValuePair("TPL_username", username));
		nvPairs.add(new BasicNameValuePair("TPL_password", password));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}

	private HttpEntity getPostEntity(DefaultHttpClient client) throws Exception {
		String username = "pis1002";
		String password = "pis1234";
		String ua = "186fCJmZk4PGRVHHxtNZngkZ3k+aC52PmgTKQ==|fyJ6Zyd9OWAiY3wpa3UsaBk=|fiB4D150Q1JSSgMWB1MdRUsAQR9Uc24mJT5vcCZXeQ==|eSRiYjNhIHA2cmY0eW03e2IheDp9NHltOXpvMXBoKXo+YipofDkQ|eCVoaEARTxFZARBbDU9RSAsgZDpxXHU=|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVEQgsKQxsJBRUdAh9XVEUbUVYPHkBGXVhJSVxNDwoMRVVCb3NxaXJ9fS8zYnw6alkVVxYCI3JmP3kTExgfWFxJAAUQWRsMT1EXVwtJezE5KmVnNihuPwsBEAhCR1IXEEZGF1Z5TQ==|dCtzBEAaRB1BBBBPCAJYHwRbCUgXUQkYSQUPUhUJTgFEG1N6AQ==|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWFrPGR6JmcWOA==|cCpyBVRjQWRLTwgcG0pJTwcreSlsK2xmMWl+Im51W2U=|cylxBldgQmdITAsfGElKTAQoeipvJ2BqPWVyKG1xX2E=|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKmdtOmJ2KmhxX2E=|bTdvGEl+XHlWUhUBBldUUho2ZDRxNHB6LXVhPHBkSnQ=|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwNXB6LXVhOXVgTnA=|bzVtGkt8XntUUBcDBFVWUBg0ZjZzNnB6LXVhNXhiTHI=|bjdvGElgTXdyZC8hIVRMWQEbG0sOJWl6LHRgPXEAXwxSGUsTAV8bDD0U|aTJqHUx7WXxTVxAEA1JRVx8zYTF0NGx+IGVxQGk=|aDNrHE1kSXN2YCslJVBIXQUfH08KSxMBXxgKOxI=|azN3azRnOWI+e2k/Z3IraGgudCp0MWl+KGhiP2d6P2wyYiJ6bjxkcChwbi5hIXomYHc8emwsaXwjdylzM2t/IQg=";
		ua = "088fCJmZk4PGRVHHxtIY30hYnw7bStzO20WLA==|fyJ6Zyd9OWAoaHkqbXIuaBk=|fiB4D157YHtufDUqfHY4fmAxfi4QCRNRWVoPQWgZ|eSRiYjNhIHA2cmw/fG4xfWgqczF2PnJmMH1oMXNtKn4+YiNucTQd|eCVoaEAQThVQFB5KEhYZAkARLwA=|ey93eSgW|ei93eSgW|dSpyBUMVSxJOCxhOAwlXEQ9KBUYaWRsRTgweXhsCQhdJGFsYY1k=|dCltbTwERRRWEwVOZmI/eG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU=|dy11AlMSTBRTCx1WFQRQFgMtEw==|di52AVARTx9cEBhMAR5LCRZWAkgYWBoORQARSxEFRhVXDk0JGkwPHEcLC0ERVAhBaBM=|cStzBFUUShJVDRtQFQRaHwpNc1w=|cCpyBVQVSxNUDBpRFAVbGQxLdVo=|cytzBFUUShpZHxdHChVADBdTCUkZXxMCSQ8YQBoFTxVeDk0BHk8PEUkOFUoYUwtODhkjCg==|cipyBVQVSxdXFh5MAR5LCBFXDEsbWBsIQwYQUhUAShBbC00IFkQDFUsIF0gZXwdBAhIoAQ==|bTRsG0pYCE1mKjxoMiZ6PiJgMXEobys+aC4kfzw+eCJpOXA8Ln4+KXcyLwFODEkbTUcZVUoeWkJsUg==|bDVtGktZCUxnKztkPi96NyJpPXgoaic3aCshdTEzdy1mNn8+LHk/KnA3KWJcAlsHUVUeU0wZVEwPMR4=|bzZuGUhaCk9kKDhnPSx5NCFqPnsraSQ0aygidjIwdC5lNXw9L3o8KXM0KmFfAVgEUlYdWE4SVksOWXZC|bjZybjFiPGc7fmw6YncubW0rcS9xNGx4M35qKmx3KH0jeD1lcSJ6by9paSt4JnYqZh0=";
		String url = "https://login.taobao.com/member/login.jhtml?from=taobaoindex&sub=true&redirect_url=http%3A%2F%2Fshu.taobao.com%2Flogin%2Fcallback";
		HttpGet get = new HttpGet(url);
		client.getCookieStore().addCookie(new BasicClientCookie("cna", "7XG9CymsEX4CATr2tV61Z/Zb"));
		HttpResponse res = client.execute(get);
		String html = EntityUtils.toString(res.getEntity(), "UTF-8");
		get.abort();
		Set<String> keySet = new HashSet<String>();
		keySet.add("allp");
		keySet.add("callback");
		keySet.add("css_style");
		keySet.add("CtrlVersion");
		keySet.add("fc");
		keySet.add("from");
		keySet.add("from_encoding");
		keySet.add("full_redirect");
		keySet.add("guf");
		keySet.add("gvfdcname");
		keySet.add("gvfdcre");
		keySet.add("isIgnore");
		keySet.add("llnick");
		keySet.add("loginsite");
		keySet.add("loginType");
		keySet.add("minipara");
		keySet.add("minititle");
		keySet.add("naviVer");
		keySet.add("need_check_code");
		keySet.add("need_sign");
		keySet.add("need_user_id");
		keySet.add("newlogin");
		keySet.add("not_duplite_str");
		keySet.add("oslanguage");
		keySet.add("osVer");
		keySet.add("popid");
		keySet.add("poy");
		keySet.add("pstrong");
		keySet.add("sign");
		keySet.add("sr");
		keySet.add("style");
		keySet.add("sub");
		keySet.add("support");
		keySet.add("tid");
		keySet.add("TPL_checkcode");
		keySet.add("TPL_password");
		keySet.add("TPL_redirect_url");
		keySet.add("TPL_username");
		keySet.add("ua");
		keySet.add("umto");
		Document dom = Jsoup.parse(html, url);
		Elements elements = dom.select("div.submit:has(#J_SubmitStatic) input[name]");
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		for (int i = 0; i < elements.size(); i++) {
			Element ele = elements.get(i);
			String name = ele.attr("name");
			String value = "";
			if (ele.hasAttr("value")) {
				value = ele.attr("value");
			}
			if (keySet.contains(name)) {
				nvPairs.add(new BasicNameValuePair(name, value));
			}
		}
		nvPairs.add(new BasicNameValuePair("TPL_checkcode", ""));
		nvPairs.add(new BasicNameValuePair("TPL_password", password));
		nvPairs.add(new BasicNameValuePair("TPL_username", username));
		nvPairs.add(new BasicNameValuePair("ua", ua));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}
}
