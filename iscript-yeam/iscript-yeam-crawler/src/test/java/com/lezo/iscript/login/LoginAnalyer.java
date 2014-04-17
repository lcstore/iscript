package com.lezo.iscript.login;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.lezo.iscript.crawler.http.GzipHttpRequestInterceptor;
import com.lezo.iscript.crawler.http.GzipHttpResponseInterceptor;
import com.lezo.iscript.crawler.http.HttpParamsConstant;
import com.lezo.iscript.crawler.http.UserAgentManager;

public class LoginAnalyer {
	@Test
	public void testProxy() throws Exception {
		DefaultHttpClient client = createHttpClient();
		addProxy(client);
		HttpContext cx = createHttpContext();
		HttpGet newGet = new HttpGet("http://cn-proxy.com/");
		HttpResponse response = client.execute(newGet, cx);
		System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
	}

	@Test
	public void test() throws Exception {
		DefaultHttpClient client = createHttpClient();
		 addProxy(client);
		HttpContext cx = createHttpContext();
		CookieStore cxStore = (CookieStore) cx.getAttribute(ClientContext.COOKIE_STORE);
		String passUrl = "http://passport.jd.com/uc/login?ltype=logout";
		HttpGet get = new HttpGet(passUrl);
		HttpResponse res = client.execute(get, cx);
		String html = EntityUtils.toString(res.getEntity(), "gbk");
		get.abort();
		Document getDom = Jsoup.parse(html, passUrl);
		Elements oElements = getDom.select("#uuid");
		String sUUID = oElements.first().attr("value");
		oElements = getDom.select("div.coagent label:contains(合作网站账号) input[name][value]");
		String randomName = oElements.first().attr("name");
		String randomValue = oElements.first().attr("value");
		oElements = getDom.select("#JD_Verification1[src]");
		if (!oElements.isEmpty()) {
			System.out.println("authcode:" + oElements.first().absUrl("src"));
		} else {
			System.out.println(getDom);
		}
		// cxStore.addCookie(new BasicClientCookie("track",
		// "1d8c488d-f0a8-c413-641f-3ab8a8f95575"));
		listCookies(client.getCookieStore(), "client cookie..");
		listCookies(cxStore, "context cookie..");
		addCookies(cxStore);
		listCookies(client.getCookieStore(), "client cookie..");
		listCookies(cxStore, "context cookie..");

		String sUrl = "https://passport.jd.com/uc/loginService?uuid=" + sUUID + "&ltype=logout&r=" + Math.random();
		System.out.println(sUrl);
		HttpPost post = new HttpPost(sUrl);
		post.addHeader("Host", "passport.jd.com");
		post.addHeader("Referer", passUrl);
		post.addHeader("Accept", "*/*");
		String useName = "lcstore@126.com";
		String password = "jd@9Lezo";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("authcode", ""));
		params.add(new BasicNameValuePair("loginname", useName));
		params.add(new BasicNameValuePair("loginpwd", password));
		params.add(new BasicNameValuePair("machineCpu", ""));
		params.add(new BasicNameValuePair("machineDisk", ""));
		params.add(new BasicNameValuePair("machineNet", ""));
		params.add(new BasicNameValuePair(randomName, randomValue));
		params.add(new BasicNameValuePair("nloginpwd", password));
		params.add(new BasicNameValuePair("uuid", sUUID));
		UrlEncodedFormEntity data = new UrlEncodedFormEntity(params, "gbk");
		data.setContentType("application/x-www-form-urlencoded");
		data.setContentEncoding("gzip, deflate");
		post.setEntity(data);

		res = client.execute(post, cx);
		System.out.println("post:" + res.getStatusLine().getReasonPhrase() + ":"
				+ EntityUtils.toString(res.getEntity(), "gbk"));
		post.abort();
		get = new HttpGet(
				"http://passport.jd.com/new/helloService.ashx?m=ls&callback=jsonp1393834927032&_=1393834927987");
		res = client.execute(get, cx);
		System.out.println("helloService:" + EntityUtils.toString(res.getEntity(), "gbk"));
		System.out.println("client cookie...");
		for (Cookie ck : client.getCookieStore().getCookies()) {
			System.out.println(ck);
		}
		System.out.println("cxStore cookie...");
		for (Cookie ck : cxStore.getCookies()) {
			System.out.println(ck);
		}

	}

	private void addProxy(DefaultHttpClient client) {
		String proxyHost = "183.129.198.231";
		proxyHost = "222.87.129.29";
		int proxyPort = 80;
		String proxyUser = null;
		String proxyPassword = null;
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		// if (proxyUser != null) {
		// CredentialsProvider credsProvider = new BasicCredentialsProvider();
		// UsernamePasswordCredentials creds = new
		// UsernamePasswordCredentials("Anonymous", "");
		// credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
		// AuthScope.ANY_PORT), creds);
		// client.getCredentialsProvider().setCredentials(new
		// AuthScope(proxy.getHostName(), proxy.getPort()),
		// new UsernamePasswordCredentials(proxyUser, proxyPassword));
		// }
	}

	private void addCookies(CookieStore cxStore) {
		BasicClientCookie newCookie = new BasicClientCookie("__jda",
				"95931165.627243178.1393824678.1393824678.1393824678.1");
		newCookie.setDomain(".jd.com");
		newCookie.setPath("/");
		cxStore.addCookie(newCookie);
		newCookie = new BasicClientCookie("__jdb", "95931165.1.627243178|1.1393824678");
		newCookie.setDomain(".jd.com");
		newCookie.setPath("/");
		cxStore.addCookie(newCookie);
		newCookie = new BasicClientCookie("__jdc", "95931165");
		newCookie.setDomain(".jd.com");
		newCookie.setPath("/");
		cxStore.addCookie(newCookie);
		newCookie = new BasicClientCookie("__jdv", "95931165|direct|-|none|-");
		newCookie.setDomain(".jd.com");
		newCookie.setPath("/");
		cxStore.addCookie(newCookie);
		newCookie = new BasicClientCookie("track", newTrackID());
		newCookie.setDomain(".jd.com");
		newCookie.setPath("/");
		cxStore.addCookie(newCookie);
		newCookie = new BasicClientCookie("__jdu", "627243178");
		newCookie.setDomain(".jd.com");
		newCookie.setPath("/");
		cxStore.addCookie(newCookie);
	}

	private void listCookies(CookieStore cxStore, String msg) {
		System.out.println(msg);
		for (Cookie ck : cxStore.getCookies()) {
			System.out.println(ck);
		}
	}

	private String a() {
		Integer rand = (int) (65536 * (1 + Math.random()));
		rand = 0 | rand;
		return Integer.toHexString(rand).substring(1);
	};

	private String newTrackID() {
		return a() + a() + "-" + a() + "-" + a() + "-" + a() + "-" + a() + a() + a();
	}

	private DefaultHttpClient createHttpClient() {
		ClientConnectionManager conman = createClientConnManager();
		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		// client.setHttpRequestRetryHandler(new
		// SimpleHttpRequestRetryHandler());
		client.addRequestInterceptor(new GzipHttpRequestInterceptor());
		client.addResponseInterceptor(new GzipHttpResponseInterceptor());
		return client;
	}

	private ClientConnectionManager createClientConnManager() {
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		supportedSchemes.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		// supportedSchemes.register(new Scheme("https", 443,
		// PlainSocketFactory.getSocketFactory()));

		try {
			SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			});
			supportedSchemes.register(new Scheme("https", 443, sf));
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ThreadSafeClientConnManager tsconnectionManager = new ThreadSafeClientConnManager(supportedSchemes);
		tsconnectionManager.setMaxTotal(HttpParamsConstant.CCM_MAX_TOTAL);
		return tsconnectionManager;
	}

	private HttpParams createHttpParams() {
		HttpParams _params = new BasicHttpParams();
		HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(_params, HttpParamsConstant.DEFAULT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(_params, true);
		// config for user agent
		String userAgent = UserAgentManager.getRadomUserAgent();
		HttpProtocolParams.setUserAgent(_params, userAgent);
		HttpClientParams.setCookiePolicy(_params, CookiePolicy.BROWSER_COMPATIBILITY);
		// set timeout
		HttpConnectionParams.setConnectionTimeout(_params, HttpParamsConstant.DEFAULT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(_params, HttpParamsConstant.DEFAULT_TIMEOUT);
		return _params;
	}

	private HttpContext createHttpContext() {
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		return localContext;
	}
}
