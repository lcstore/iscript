package com.lezo.iscript;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.junit.Test;

import com.lezo.iscript.crawler.http.GzipHttpRequestInterceptor;
import com.lezo.iscript.crawler.http.GzipHttpResponseInterceptor;
import com.lezo.iscript.crawler.http.HttpParamsConstant;
import com.lezo.iscript.crawler.http.SimpleHttpRequestRetryHandler;
import com.lezo.iscript.crawler.http.UserAgentManager;

public class ProxySelectTest {
	@Test
	public void test() throws Exception, IOException {
//		DefaultHttpClient client = createHttpClient();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet("http://item.jd.com/856850.html");
		SchemeRegistry schreg = client.getConnectionManager().getSchemeRegistry();
		ProxySelector prosel = new ProxySelector() {
			private List<Proxy> proxyList;

			@Override
			public List<Proxy> select(URI uri) {
				if (proxyList == null) {
					proxyList = new ArrayList<Proxy>();
					String host = "10.101.0.71";
					int port = 10003;
					InetAddress addr = null;
					try {
						addr = InetAddress.getByName(host);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SocketAddress sa = new InetSocketAddress(addr, port);
					Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
					proxyList.add(proxy);
				}
				return proxyList;
			}

			@Override
			public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
				// TODO Auto-generated method stub

			}
		};
		HttpRoutePlanner routePlanner = new ProxySelectorRoutePlanner(schreg, prosel);
		client.setRoutePlanner(routePlanner);
		HttpResponse rep = client.execute(request);
		System.out.println(rep.getStatusLine().getReasonPhrase());
	}

	private static void addHttpsTrustStrategy(SchemeRegistry supportedSchemes) {
		try {
			TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			};
			SSLSocketFactory sf = new SSLSocketFactory(acceptingTrustStrategy,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			supportedSchemes.register(new Scheme("https", 443, sf));
			// supportedSchemes.register(new Scheme("https", 8443, sf));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static ClientConnectionManager createClientConnManager() {
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		supportedSchemes.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		addHttpsTrustStrategy(supportedSchemes);
		// addHttpsTrustManager(supportedSchemes);
		ThreadSafeClientConnManager tsconnectionManager = new ThreadSafeClientConnManager(supportedSchemes);
		tsconnectionManager.setMaxTotal(HttpParamsConstant.CCM_MAX_TOTAL);
		return tsconnectionManager;
	}

	public static DefaultHttpClient createHttpClient() {
		ClientConnectionManager conman = createClientConnManager();
		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		client.setHttpRequestRetryHandler(new SimpleHttpRequestRetryHandler());
		CookieSpecFactory csf = getCustomCookieSpecFactory();
		client.getCookieSpecs().register("easy", csf);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

		client.addRequestInterceptor(new GzipHttpRequestInterceptor());
		client.addResponseInterceptor(new GzipHttpResponseInterceptor());
		return client;
	}

	private static HttpParams createHttpParams() {
		HttpParams _params = new BasicHttpParams();
		HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(_params, HttpParamsConstant.DEFAULT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(_params, true);
		// config for user agent
		String userAgent = UserAgentManager.getRadomUserAgent();
		userAgent = "MicroMessenger Client";
		userAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
		HttpProtocolParams.setUserAgent(_params, userAgent);
		// HttpClientParams.setCookiePolicy(_params,
		// CookiePolicy.BROWSER_COMPATIBILITY);

		// set timeout
		HttpConnectionParams.setConnectionTimeout(_params, HttpParamsConstant.DEFAULT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(_params, HttpParamsConstant.DEFAULT_TIMEOUT);
		return _params;
	}

	private static CookieSpecFactory getCustomCookieSpecFactory() {
		CookieSpecFactory csf = new CookieSpecFactory() {
			public CookieSpec newInstance(HttpParams params) {
				return new BrowserCompatSpec() {
					@Override
					public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
						// Oh, I am easy
					}
				};
			}
		};
		return csf;
	}
}
