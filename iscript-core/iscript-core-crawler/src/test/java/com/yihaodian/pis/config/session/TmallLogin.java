package com.yihaodian.pis.config.session;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class TmallLogin {

	public void doLogin(DefaultHttpClient client, String userName, String pwd) throws Exception {
		String uri = "https://login.taobao.com/member/login.jhtml";
		String tid = "XOR_1_000000000000000000000000000000_63584751370E7B737371717D";
		String ua = "186fCJmZk4PGRVHHxtNZngkZ3k+aC52PmgTKQ==|fyJ6Zyd9OWAiY3wpa3UsaBk=|fiB4D150Q1JSSgMWB1MdRUsAQR9Uc24mJT5vcCZXeQ==|eSRiYjNhIHA2cmY0eW03e2IheDp9NHltOXpvMXBoKXo+YipofDkQ|eCVoaEARTxFZARBbDU9RSAsgZDpxXHU=|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVEQgsKQxsJBRUdAh9XVEUbUVYPHkBGXVhJSVxNDwoMRVVCb3NxaXJ9fS8zYnw6alkVVxYCI3JmP3kTExgfWFxJAAUQWRsMT1EXVwtJezE5KmVnNihuPwsBEAhCR1IXEEZGF1Z5TQ==|dCtzBEAaRB1BBBBPCAJYHwRbCUgXUQkYSQUPUhUJTgFEG1N6AQ==|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWFrPGR6JmcWOA==|cCpyBVRjQWRLTwgcG0pJTwcreSlsK2xmMWl+Im51W2U=|cylxBldgQmdITAsfGElKTAQoeipvJ2BqPWVyKG1xX2E=|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKmdtOmJ2KmhxX2E=|bTdvGEl+XHlWUhUBBldUUho2ZDRxNHB6LXVhPHBkSnQ=|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwNXB6LXVhOXVgTnA=|bzVtGkt8XntUUBcDBFVWUBg0ZjZzNnB6LXVhNXhiTHI=|bjdvGElgTXdyZC8hIVRMWQEbG0sOJWl6LHRgPXEAXwxSGUsTAV8bDD0U|aTJqHUx7WXxTVxAEA1JRVx8zYTF0NGx+IGVxQGk=|aDNrHE1kSXN2YCslJVBIXQUfH08KSxMBXxgKOxI=|azN3azRnOWI+e2k/Z3IraGgudCp0MWl+KGhiP2d6P2wyYiJ6bjxkcChwbi5hIXomYHc8emwsaXwjdylzM2t/IQg=";
		HttpPost requset = new HttpPost(uri);
		List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		pairList.add(new BasicNameValuePair("loginsite", "0"));
		pairList.add(new BasicNameValuePair("support", "000001"));
		pairList.add(new BasicNameValuePair("callback", "1"));
		pairList.add(new BasicNameValuePair("CtrlVersion", "1,0,0,7"));
		pairList.add(new BasicNameValuePair("loginType", "3"));
		pairList.add(new BasicNameValuePair("pstrong", "3"));
		pairList.add(new BasicNameValuePair("sr", "1311*737"));
		pairList.add(new BasicNameValuePair("fc", "default"));
		pairList.add(new BasicNameValuePair("style", "default"));
		pairList.add(new BasicNameValuePair("naviVer", "ie|8"));
		pairList.add(new BasicNameValuePair("TPL_username", userName));
		pairList.add(new BasicNameValuePair("TPL_password", pwd));
		pairList.add(new BasicNameValuePair("umto", "T33352b00721774513259a5b52c805990"));
		pairList.add(new BasicNameValuePair("from", "tb"));
		pairList.add(new BasicNameValuePair("osVer", "windows|5.1"));
		pairList.add(new BasicNameValuePair("tid", "XOR_1_000000000000000000000000000000_175843533008787508037579"));
		pairList.add(new BasicNameValuePair("newlogin", "1"));
		pairList.add(new BasicNameValuePair("ua", ua));
		requset.setEntity(new UrlEncodedFormEntity(pairList, "UTF-8"));
		HttpResponse response = client.execute(requset);
		HttpEntity entity = response.getEntity();
		String content = EntityUtils.toString(entity);
		EntityUtils.consume(entity);
		System.out.println(content);
		JSONObject jObj = new JSONObject(content);
		if (!jObj.getBoolean("state")) {
			throw new RuntimeException("Login fail,msg:" + content);
		} else {
			System.err.println("login JSON:" + content);
			String token = jObj.getJSONObject("data").getString("token" + "");
			// String token = "m9b3IGr4oim";
			String url = "https://passport.alipay.com/mini_apply_st.js?site=0&token=" + token
					+ "&callback=vstCallback129";
			System.out.println("url:" + url);
			HttpUriRequest payGet = new HttpGet(url);
			response = client.execute(payGet);
			content = EntityUtils.toString(response.getEntity());
			System.out.println(content);
			EntityUtils.consume(response.getEntity());
			// vstCallback129({"data":{"st":"1SjmoNLUFHpKr6laqPRLd0w"},"code":200});
			Pattern oReg = Pattern.compile("(data).*?(st).*?([0-9a-zA-Z_\\-]+).*?(code).*?([0-9]+)");
			Matcher matcher = oReg.matcher(content);
			String stCode = null;
			String rCode = null;
			if (matcher.find(3)) {
				stCode = matcher.group(3);
				if (matcher.find(5)) {
					rCode = matcher.group(5);
				}
			}
			if (stCode != null && rCode != null) {
				if (Integer.valueOf(rCode) < 300) {
					String encodeUName = URLEncoder.encode(userName, "utf-8");
					String params = "&style=default&sub=&TPL_username="
							+ encodeUName
							+ "&loginsite=0&from_encoding=&not_duplite_str=&guf=&full_redirect=&isIgnore=&need_sign=&sign=&from=tb&TPL_redirect_url=";
					long nowTime = new Date().getTime();
					String _ksTS = "&" + nowTime + "_141";
					String sCallback = "&callback=jsonp142";
					String sLogin = "https://login.taobao.com/member/vst.htm?st=" + stCode + params + sCallback + _ksTS;

					HttpUriRequest loginGet = new HttpGet(sLogin);
					response = client.execute(loginGet);
					content = EntityUtils.toString(response.getEntity());
					EntityUtils.consume(response.getEntity());
					String sFun = "function getData(data){return data;}";
					String html = sFun + " var oData= " + content.replace("jsonp142", "return getData");
					html += "var myUrl = oData.data.url;";
					Context cx = Context.enter();
					Scriptable scope = cx.initStandardObjects();
					cx.evaluateString(scope, html, "<script>", 1, null);
					String myUrl = (String) scope.get("myUrl", scope);
					loginGet = new HttpGet(myUrl);
					response = client.execute(loginGet);
					EntityUtils.consume(response.getEntity());
				}
			}
		}
	}
}
