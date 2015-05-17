package com.lezo.iscript.login;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.lezo.iscript.rest.http.GzipHttpRequestInterceptor;
import com.lezo.iscript.rest.http.GzipHttpResponseInterceptor;
import com.lezo.iscript.rest.http.HttpParamsConstant;
import com.lezo.iscript.rest.http.UserAgentManager;

public class HttpBase {
	public static void addProxy(DefaultHttpClient client) {
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

	public static void addCookies(CookieStore cxStore) {
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

	public static void listCookies(CookieStore cxStore, String msg) {
		System.out.println(msg);
		for (Cookie ck : cxStore.getCookies()) {
			System.out.println(ck);
		}
	}

	public static String a() {
		Integer rand = (int) (65536 * (1 + Math.random()));
		rand = 0 | rand;
		return Integer.toHexString(rand).substring(1);
	};

	public static String newTrackID() {
		return a() + a() + "-" + a() + "-" + a() + "-" + a() + "-" + a() + a() + a();
	}

	public static DefaultHttpClient createHttpClient() {
		ClientConnectionManager conman = createClientConnManager();
		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		// client.setHttpRequestRetryHandler(new
		// SimpleHttpRequestRetryHandler());
		client.addRequestInterceptor(new GzipHttpRequestInterceptor());
		client.addResponseInterceptor(new GzipHttpResponseInterceptor());
		return client;
	}

	public static ClientConnectionManager createClientConnManager() {
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
		tsconnectionManager.setMaxTotal(HttpParamsConstant.MAX_TOTAL_CONNECTIONS);
		return tsconnectionManager;
	}

	public static HttpParams createHttpParams() {
		HttpParams _params = new BasicHttpParams();
		HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(_params, HttpParamsConstant.DEFAULT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(_params, true);
		// config for user agent
		String userAgent = UserAgentManager.getRadomUserAgent();
		userAgent = "MicroMessenger Client";
		userAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
		HttpProtocolParams.setUserAgent(_params, userAgent);
		HttpClientParams.setCookiePolicy(_params, CookiePolicy.BROWSER_COMPATIBILITY);
		// set timeout
		HttpConnectionParams.setConnectionTimeout(_params, HttpParamsConstant.DEFAULT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(_params, HttpParamsConstant.DEFAULT_TIMEOUT);
		return _params;
	}

	public static HttpContext createHttpContext() {
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		return localContext;
	}

	public static String getContent(DefaultHttpClient client, HttpUriRequest get) throws Exception {
		return getContent(client, get, "UTF-8");
	}

	public static String getContent(DefaultHttpClient client, HttpUriRequest get, String charsetName) throws Exception {
		HttpResponse res = client.execute(get);
		String html = EntityUtils.toString(res.getEntity(), charsetName);
		get.abort();
		return html;
	}
}
