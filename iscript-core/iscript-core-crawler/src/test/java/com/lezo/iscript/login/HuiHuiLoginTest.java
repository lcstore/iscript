package com.lezo.iscript.login;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class HuiHuiLoginTest {

	@Test
	public void testLogin() throws Exception {
		String postUrl = "https://reg.163.com/logins.jsp";
		DefaultHttpClient client = HttpBase.createHttpClient();
		HttpPost post = new HttpPost();
		post.setURI(new URI(postUrl));
		HttpEntity postEntity = getPostEntity();
		post.setEntity(postEntity);
		HttpResponse res = client.execute(post);
		String html = EntityUtils.toString(res.getEntity(), "UTF-8");
		Document dom = Jsoup.parse(html, postUrl);
		String getUrl = getLocationUrl(dom);
		post.abort();
		HttpGet get = new HttpGet(getUrl);
		res = client.execute(get);
		html = EntityUtils.toString(res.getEntity(), "UTF-8");
		get.abort();
		dom = Jsoup.parse(html, getUrl);
		getUrl = getLocationUrl(dom);
		System.out.println(getUrl);
		String uinfoUrl = "http://www.huihui.cn/u/info.json?_=1395676815703";
		get = new HttpGet(uinfoUrl);
		res = client.execute(get);
		html = EntityUtils.toString(res.getEntity(), "UTF-8");
		get.abort();
		System.out.println("login,uinfo:" + html);

		String checkinUrl = "http://www.huihui.cn/checkin";
		post = new HttpPost(checkinUrl);
		res = client.execute(post);
		html = EntityUtils.toString(res.getEntity(), "UTF-8");
		post.abort();
		System.out.println("checkin:" + html);
	}

	private String getLocationUrl(Document dom) {
		Elements elements = dom.select("script[language=JavaScript]");
		String sMark = "window.location.replace";
		String getUrl = null;
		for (int i = 0; i < elements.size(); i++) {
			String sHtml = elements.get(i).html();
			if (sHtml.indexOf(sMark) > -1) {
				getUrl = getLocationUrl(sHtml);
				break;
			}
		}
		return getUrl;
	}

	private String getLocationUrl(String sHtml) {
		String sLocationUrl = "var locationUrl;";
		String sObj = "var window={};window.location={};";
		String sFun = "window.location.replace=function(sUrl){locationUrl =sUrl;};";
		Context cx = Context.enter();
		String source = sLocationUrl + sObj + sFun + sHtml;
		Scriptable scope = cx.initStandardObjects();
		cx.evaluateString(scope, source, "cmd", 0, null);
		return Context.toString(scope.get("locationUrl", scope));
	}

	private HttpEntity getPostEntity() throws Exception {
		String username = "lcstore@126.com";
		String password = "126@9Lezo";
		username = "lcstore@126.com";
		password = "126@9Lezo";
		String url = "http://www.huihui.cn/activate?url=http%3A%2F%2Fwww.huihui.cn%2Flogin";
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		nvPairs.add(new BasicNameValuePair("domains", "huihui.cn"));
		nvPairs.add(new BasicNameValuePair("product", "huihui"));
		nvPairs.add(new BasicNameValuePair("savelogin", "1"));
		nvPairs.add(new BasicNameValuePair("type", "1"));
		nvPairs.add(new BasicNameValuePair("url", url));
		nvPairs.add(new BasicNameValuePair("username", username));
		nvPairs.add(new BasicNameValuePair("password", password));
		return new UrlEncodedFormEntity(nvPairs, "utf-8");
	}
}
