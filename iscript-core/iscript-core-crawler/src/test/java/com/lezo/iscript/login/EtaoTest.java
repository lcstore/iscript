package com.lezo.iscript.login;

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
import org.apache.http.impl.client.DefaultHttpClient;
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

public class EtaoTest {

	private static final String CHARSET_NAME = "utf-8";

	@Test
	public void test() throws Exception {
		String username = "ajane90";
		String password = "aj891209";
		username = "lcstore@126.com";
		password = "taobao@9Lezo";
		String ua = "186fCJmZk4PGRVHHxtNZngkZ3k+aC52PmgTKQ==|fyJ6Zyd9OWAiY3wpa3UsaBk=|fiB4D150Q1JSSgMWB1MdRUsAQR9Uc24mJT5vcCZXeQ==|eSRiYjNhIHA2cmY0eW03e2IheDp9NHltOXpvMXBoKXo+YipofDkQ|eCVoaEARTxFZARBbDU9RSAsgZDpxXHU=|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVEQgsKQxsJBRUdAh9XVEUbUVYPHkBGXVhJSVxNDwoMRVVCb3NxaXJ9fS8zYnw6alkVVxYCI3JmP3kTExgfWFxJAAUQWRsMT1EXVwtJezE5KmVnNihuPwsBEAhCR1IXEEZGF1Z5TQ==|dCtzBEAaRB1BBBBPCAJYHwRbCUgXUQkYSQUPUhUJTgFEG1N6AQ==|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWFrPGR6JmcWOA==|cCpyBVRjQWRLTwgcG0pJTwcreSlsK2xmMWl+Im51W2U=|cylxBldgQmdITAsfGElKTAQoeipvJ2BqPWVyKG1xX2E=|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKmdtOmJ2KmhxX2E=|bTdvGEl+XHlWUhUBBldUUho2ZDRxNHB6LXVhPHBkSnQ=|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwNXB6LXVhOXVgTnA=|bzVtGkt8XntUUBcDBFVWUBg0ZjZzNnB6LXVhNXhiTHI=|bjdvGElgTXdyZC8hIVRMWQEbG0sOJWl6LHRgPXEAXwxSGUsTAV8bDD0U|aTJqHUx7WXxTVxAEA1JRVx8zYTF0NGx+IGVxQGk=|aDNrHE1kSXN2YCslJVBIXQUfH08KSxMBXxgKOxI=|azN3azRnOWI+e2k/Z3IraGgudCp0MWl+KGhiP2d6P2wyYiJ6bjxkcChwbi5hIXomYHc8emwsaXwjdylzM2t/IQg=";
//		ua = "093fCJmZk4PGRVHHxtNZ+ngkZ3k+aC52PmgTKQ==|fyJ6Zyd9OWEhYHIibnoiZxY=|fiB4D150Q1JSSgMWB1MdRUsAQR9Uc24mJT5vcCZXeQ==|eSRiYjNhIHA2c2U3d2YydGwudzVyNXJsMn5oMnZvKn0+YytpbVc=|eCVoaEAQThZXGw9EARNTBQ4RBBcnEx5dXl4UXwcZSHZZ|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVEQgsKQxsJBRUdAh9XVEUbUVYPHkBGXVhJSVxNDwoMRVVCb3NxaXJ9fS8zYnw6alkVVxYCI3JmP3kTExgfWFxJAAUQWRsMT1EXVwtJezE5KmVnNihuPwsBEAhCR1IXEEZGF1Z5TQ==|dCtzBEAaRB1BBBBPCAJYGQBfDUwTVQ0cTQELVhENSgVAH1d+BQ==|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWFrPGRzKm50WmQ=|cCpyBVRjQWRLTwgcG0pJTwcreSlsK2xmMWl+JWlyXGI=|cylxBldgQmdITAsfGElKTAQoeipvJ2BqPWVxLW5yXGI=|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKmdtOmJ2L2J8Umw=|bTdvGEl+XHlWUhUBBldUUho2ZDRxNHB6LXVhOn5gTnA=|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwNXB6LXVgPH9hT3E=|bzVtGkt8XntUUBcDBFVWUBg0ZjZzNnB6LXVgOX1mSHY=|bjdvGElgTXdyZC8hIVRMWQEbG0sOJWl4LnZiOHsKVQZYE0EZClwbCDkQ|aTJqHUx7WXxTVxAEA1JRVx8zYTF0NGx/KWl6S2I=|aDNrHE1kSXN2YCslJVBIXQUfH08KSxMAVhUCMxo=|azN3azRnOWI+e2k/Z3IraGgudCp0MWl+KGhiP2d6P2wyYiJ6bjxkcChwbi5hIXomYHc8emwsaXwjdylzM2t/IQg=";
//		ua = "235fCJmZk4PGRVHHxtEb3EtbnA3YSd/N2EaIA==|fyJ6Zyd9OWEobH0tYXIubh8=|fiB4D150Q1JSSgMWB1MdRUsBQB5Vcm8nJD9ucSdWeA==|eSRiYjNhIHA2c2w7eGk9fGAnfjx7OX5uMXNkPHpmJ3w9bS9qeCx6AQ==|eCVoaEATTRdeHxVAGBxbW1V7RQ==|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVRUQ4OHw4QGgYGFw9FBBZFRV0uLyk/Pz0saXozYX9+N2JiY3lWUQMdBUYXBl5MQBQZDB5ZS1MBMippdHBdWEdUGxJbTAYUSlZYFxsfCkMCE1tCRldQQUZKHBxKQWo+NydiY2V0aUZHExhRHwcHWw1cG0FwITVnIUEITlcWMy0rPCFpIDFjbnV3Z2t0OHo7Lw4WX1RJCxUfCU5WVh0WR1MKTScmIDY2NCVgc1gLFQ0ERRNSf2dnNSJzZz54YSEDVA9KDB9KeHslZHo8bVlaTBZVRlMaWE8MElQUSApLX34vO2IkPX1YCE0fSTII|dCtzBEUUVBFWFwhDBBZIEAlKGUcfXB4ORQYWQhoHRxRSF1ASDDYf|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWVvOGB3K2l0Mw0i|cCpyBVRjQWRLTwgcG0pJTwcreSlsKmthNm55JWh1Mgwj|cylxBldgQmdITAsfGElKTAQoeipvKGVvOGB3KmxxNwkm|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKGpgN294JWR9Nwkm|bTdvGEl+XHlWUhUBBldUUho2ZDRxN3Z8K3NkOXpvJRs0|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwN3pwJ39oNnNnJBo1|bzVtGkt8XntUUBcDBFVWUBg0ZjZzMnB6LXViOHRqLhA/|bjRsG0p9X3pVURYCBVRXURk1ZzdyM3F7LHRjOH9hKhQ7|aTNrHE16WH1SVhEFAlNQVh4yYDB1NHZ8K3NkP3hiKBY5|aDJqHUx7WXxTVxAEA1JRVx8zYTF0NXd9KnJlPn5gJBo1|azFpHk94Wn9QVBMHAFFSVBwwYjJ3NnR+KXFmPX1lLhA/|ajBoH055W35RVRIGAVBTVR0xYzN2N3V/KHBnPH1hKxU6|ZT9nEEF2VHFeWh0JDl9cWhI+bDx5OHpwJ39oM3JtLxE+|ZD5mEUB3VXBfWxwID15dWxM/bT14OXtxJn5pMnNmJhg3|Zz1lEkN0VnNcWB8LDF1eWBA8bj57OnhyJX1qMXNuLBI9|ZjxkE0J1V3JdWR4KDVxfWRE9bz96P3txInptNnRrKhQ7|YTtjFEVyUHVaXhkNCltYXhY6aDh9OHtxJn5pPXtnJxk2|YDpiFURzUXRbXxgMC1pZXxc7aTl8PX91InptOXtmLRM8|YzlhFkdwUndYXBsPCFlaXBQ4ajp/OHpwI3tsOHRrIB4x|YjhgF0ZxU3ZZXRoOCVhbXRU5azt+OHV/KHBnMndsJxk2|XQdfKHlObElmYiUxNmdkYioGVARBCExGEUleC0pREC4B|XAZeKXhPbUhnYyQwN2ZlYysHVQVAB0pAF09YDUFfHyEO|XwRcK3pNb0plYSYyNWRnYSkFVwdCAlpOG19PGjNC|XgRcK3pNb0plYSYyNWRnYSkFVwdCA0BKHUVSB0BcH0VqXg==|WQNbLH1KaE1iZiE1MmNgZi4CUABFBEdNGkJRD0NZHkxjVw==|WAJaLXxLaUxjZyA0M2JhZy8DUQFEBUZMG0NQBUlUFUFuWg==|WwJaLXxVeEJHURoUFGF5bDQuLn47EFVDE0tfAkU0azhmLX8nN2MuOmclVHo=|WgFZLn9We0FEUhkXF2J6bzctLX04eSExZSg8YiFQfg==|VQ1JVQpZB1wARVcBWUwVVlYQShRKD1dAFlZcAVlEAVIMXBxEUAJaThZOUBBfH0QYXkkCRFISV0IdTwVAAUJIHlxWC0pKCF8BWRpCVQBYTxJKVxRbEFUdRVQfXVcOVk4RQwdCAFhPBEBKC05QD10eKg==";
		ua = "152fCJmZk4PGRVHHxtEb3EtbnA3YSd/N2EaIA==|fyJ6Zyd9OWYuaHwuaXktbx4=|fiB4D150Q1JSSgMWB1MdRUsBQB5Vcm8nJD9ucSdWeA==|eSRiYjNhIHA2dGo/eWo1dG8tdDZxNHlvOndkPHBuLXg+bipoez4X|eCVoaEASTBRVFARPCAJMFAlTEj0J|ey93eSgW|ei93eSgW|dSpyBUEbRRxABRFODQdYHQdAD0wWUBIYSAoUVBEOTRpEGlwQa1E=|dCltbTwERRRTHghDa28ydWAlcDphIW16Lml/byspazx9ImQjMWIhNmovNnMpbjQYTkQSOz8O|dy52AVART307eGohZ3UtdWshbi1yN29+LGhiPXllSwRGA1EHDVISBzYf|di52AVARTxdWEARPCB9LEwZMHFtvQg==|cSlxBlcWSBBQFABLDRlEHAJFFFABLAU=|cChwB1YXSRFRFgZNCB9FHQNIHl8EKQA=|cypyBVQVS3k4fGk3b3ggZxZJGkQPXQURTwsaRG0c|cipyBVQVSxNTHglCBBZNFQpJGlANIAk=|bTRsG0oLVWcmY3UgeG0zdQRbCFYdTxcCVRcFWHEA|bDRwbDNgPmU5fG44YHUsb28pcy1zNm56MXxoKG13KH0jfyNjaT95cy11aCsV";
		ua = "223fCJmZk4PGRVHHxtEb3EtbnA3YSd/N2EaIA==|fyJ6Zyd9OWYub3Evb3ktYRA=|fiB4D150Q1JSSgMWB1MdRUsBQB5Vcm8nJD9ucSdWeA==|eSRiYjNhIHA2dGo4dGs0dmwmfz16M3BjMndjOHRsLn48ZyJgdzIb|eCVoaEARTxNRCRxXAWJ+a2VLdQ==|ey93eSgW|ei93eSgW|dSpyBUEbRRxABRFODQdYHQdAD0wWUBIYSAoUVBEOTRpEGlwQa1E=|dCltbTwERRRTHghDa28ydWAlcDphIW16Lml/byspazx9ImQjMWIhNmovNnMpbjQYTkQSOz8O|dy93AFEQThZWFQpBBhFJEQ9NGlltQA==|di93AFEQTnw9eWY4YHUobx5BEkwHVQ0ZTQ8dLAU=|cSltcS59I3gkYXMlfWgxcnI0bjBuK3NnLGF1NXZ2NGJN";
		DefaultHttpClient client = HttpBase.createHttpClient();
		addTabaoToken(client);
		HttpUriRequest mLoginGet = getMemberLogin(client);
		String html = HttpBase.getContent(client, mLoginGet);
		Document dom = Jsoup.parse(html);
		// TODO get
		if (needcode(client, mLoginGet.getURI().toURL(), username)) {
			return;
		}
		HttpEntity postEntity = getPostEntity(dom, username, password, ua);
		// System.out.println(EntityUtils.toString(postEntity));
		HttpPost postLogin = new HttpPost("https://login.taobao.com/member/login.jhtml");
		postLogin.setEntity(postEntity);
		html = HttpBase.getContent(client, postLogin);
		JSONObject loginObject = new JSONObject(html);
		if (!loginObject.getBoolean("state")) {
			System.err.println(loginObject);
			return;
		}
		System.out.println(html);
		String token = loginObject.getJSONObject("data").getString("token");
		String st = getStValue(client, token);
		System.out.println("st:" + st);
		String vstUrl = getVstUrl(dom, username) + "&st=" + st;
		String callBackUrl = getCallBackUrl(client, vstUrl);
		html = HttpBase.getContent(client, new HttpGet(callBackUrl));
		// System.out.println("html:" + html);
		ua = "152fCJmZk4PGRVHHxtEb3EtbnA3YSd%2FN2EaIA%3D%3D%7CfyJ6Zyd9OWYuaHwuaXktbx4%3D%7CfiB4D150Q1JSSgMWB1MdRUsBQB5Vcm8nJD9ucSdWeA%3D%3D%7CeSRiYjNhIHA2dGo%2FeWo1dG8tdDZxNHlvOndkPHBuLXg%2Bbipoez4X%7CeCVoaEASTBRVFARPCAJMFAlTEj0J%7Cey93eSgW%7Cei93eSgW%7CdSpyBUEbRRxABRFODQdYHQdAD0wWUBIYSAoUVBEOTRpEGlwQa1E%3D%7CdCltbTwERRRTHghDa28ydWAlcDphIW16Lml%2Fbyspazx9ImQjMWIhNmovNnMpbjQYTkQSOz8O%7Cdy52AVART307eGohZ3UtdWshbi1yN29%2BLGhiPXllSwRGA1EHDVISBzYf%7Cdi52AVARTxdWEARPCB9LEwZMHFtvQg%3D%3D%7CcSlxBlcWSBBQFABLDRlEHAJFFFABLAU%3D%7CcChwB1YXSRFRFgZNCB9FHQNIHl8EKQA%3D%7CcypyBVQVS3k4fGk3b3ggZxZJGkQPXQURTwsaRG0c%7CcipyBVQVSxNTHglCBBZNFQpJGlANIAk%3D%7CbTRsG0oLVWcmY3UgeG0zdQRbCFYdTxcCVRcFWHEA%7CbDRwbDNgPmU5fG44YHUsb28pcy1zNm56MXxoKG13KH0jfyNjaT95cy11aCsV";
		HttpGet get = new HttpGet("http://jf.etao.com/ajax/getCreditForSrp.htm?jfSource=1&ua=" + ua + "&_ksTS="
				+ System.currentTimeMillis() + "_644&callback=jsonp645");
		html = HttpBase.getContent(client, get);
		System.out.println("jf.html:" + html);
		// for (Cookie ck : client.getCookieStore().getCookies()) {
		// System.out.println(ck);
		// }
	}

	private String getCallBackUrl(DefaultHttpClient client, String vstUrl) throws Exception {
		String html = HttpBase.getContent(client, new HttpGet(vstUrl));
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
		String html = HttpBase.getContent(client, get);
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

	private boolean needcode(DefaultHttpClient client, URL refer, String username) throws Exception {
		HttpPost post = new HttpPost();
		String ua = "";
//		ua = "152fCJmZk4PGRVHHxtEb3EtbnA3YSd/N2EaIA==|fyJ6Zyd9OWYuaHwuaXktbx4=|fiB4D150Q1JSSgMWB1MdRUsBQB5Vcm8nJD9ucSdWeA==|eSRiYjNhIHA2dGo/eWo1dG8tdDZxNHlvOndkPHBuLXg+bipoez4X|eCVoaEASTBRVFARPCAJMFAlTEj0J|ey93eSgW|ei93eSgW|dSpyBUEbRRxABRFODQdYHQdAD0wWUBIYSAoUVBEOTRpEGlwQa1E=|dCltbTwERRRTHghDa28ydWAlcDphIW16Lml/byspazx9ImQjMWIhNmovNnMpbjQYTkQSOz8O|dy52AVART307eGohZ3UtdWshbi1yN29+LGhiPXllSwRGA1EHDVISBzYf|di52AVARTxdWEARPCB9LEwZMHFtvQg==|cSlxBlcWSBBQFABLDRlEHAJFFFABLAU=|cChwB1YXSRFRFgZNCB9FHQNIHl8EKQA=|cypyBVQVS3k4fGk3b3ggZxZJGkQPXQURTwsaRG0c|cipyBVQVSxNTHglCBBZNFQpJGlANIAk=|bTRsG0oLVWcmY3UgeG0zdQRbCFYdTxcCVRcFWHEA|bDRwbDNgPmU5fG44YHUsb28pcy1zNm56MXxoKG13KH0jfyNjaT95cy11aCsV";
		post.setURI(new URI("https://login.taobao.com/member/request_nick_check.do?_input_charset=utf-8"));
		post.addHeader("Referer", refer.toString());
		post.addHeader("Cache-Control", "no-cache");
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

	private HttpEntity getPostEntity(Document dom, String username, String password, String ua)
			throws UnsupportedEncodingException {
		String tid = "XOR_1_000000000000000000000000000000_63584751370E7B737371717D";
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
		String html = HttpBase.getContent(client, get);
		// System.out.println(html);
	}

	private HttpUriRequest getMemberLogin(DefaultHttpClient client) throws Exception {
		String url = "http://login.etao.com/?spm=0.0.0.0.NTyQZP&redirect_url=http%3A%2F%2Fjf.etao.com%2F%3F";
		HttpGet get = new HttpGet(url);
//		get.addHeader("Referer", "http://jf.etao.com/?");
		String html = HttpBase.getContent(client, get);
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
