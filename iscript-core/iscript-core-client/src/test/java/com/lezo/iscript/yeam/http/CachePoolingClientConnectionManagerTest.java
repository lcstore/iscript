package com.lezo.iscript.yeam.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.lezo.iscript.crawler.http.HttpParamsConstant;
import com.lezo.iscript.crawler.http.SimpleHttpRequestRetryHandler;
import com.lezo.iscript.crawler.http.UserAgentManager;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年12月4日
 */
public class CachePoolingClientConnectionManagerTest {

	@Test
	public void test() throws Exception {
		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schreg.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		addHttpsTrustStrategy(schreg);

		DnsResolver dnsResolver = new ShuffleCacheDnsResolver();
		PoolingClientConnectionManager conman = new PoolingClientConnectionManager(schreg, dnsResolver);

		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		client.setHttpRequestRetryHandler(new SimpleHttpRequestRetryHandler());
		CookieSpecFactory csf = getCustomCookieSpecFactory();
		client.getCookieSpecs().register("easy", csf);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

		// HttpGet get = new HttpGet("http://www.ip138.com/");
		// HttpGet get = new
		// HttpGet("http://detail.tmall.com/item.htm?id=37453735704");
		String url = "http://list.tmall.com/search_product.htm?spm=a220m.1000858.1000721.32.PUf5uz&cat=50100707&sort=s&style=g&from=sn_1_cat#J_crumbs";
		for (int i = 0; i < 10; i++) {
			HttpGet get = new HttpGet(url);
			get.addHeader("Referer", "https://login.taobao.com/member/login.jhtml");
			HttpResponse resp = client.execute(get);
			String html = (EntityUtils.toString(resp.getEntity(), "gbk"));
			if (html.indexOf("69724471647") > 0) {
				System.out.println(i + "=OK");
			} else {
				System.err.println(i + "=" + html);
			}
		}
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
		HttpConnectionParams.setConnectionTimeout(_params, HttpParamsConstant.CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(_params, HttpParamsConstant.READ_TIMEOUT);
		return _params;
	}

	private static void addHttpsTrustStrategy(SchemeRegistry supportedSchemes) {
		try {
			TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			};
			SSLSocketFactory sf = new SSLSocketFactory(acceptingTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			supportedSchemes.register(new Scheme("https", 443, sf));
			// supportedSchemes.register(new Scheme("https", 8443, sf));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static class MyConnectionSocketFactory implements SchemeSocketFactory {
		@Override
		public Socket createSocket(HttpParams params) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) params.getParameter("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}

		@Override
		public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
			if (remoteAddress == null) {
				throw new IllegalArgumentException("Remote address may not be null");
			}
			if (params == null) {
				throw new IllegalArgumentException("HTTP parameters may not be null");
			}
			if (sock == null) {
				sock = createSocket(params);
			}
			if (localAddress != null) {
				sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
				sock.bind(localAddress);
			}
			int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
			int soTimeout = HttpConnectionParams.getSoTimeout(params);

			try {
				sock.setSoTimeout(soTimeout);
				sock.connect(remoteAddress, connTimeout);
			} catch (SocketTimeoutException ex) {
				throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
			}
			return sock;
		}

		@Override
		public boolean isSecure(Socket sock) throws IllegalArgumentException {
			if (sock == null) {
				throw new IllegalArgumentException("Socket may not be null.");
			}
			// This check is performed last since it calls a method implemented
			// by the argument object. getClass() is final in java.lang.Object.
			if (sock.isClosed()) {
				throw new IllegalArgumentException("Socket is closed.");
			}
			return false;
		}
	}

	static class FakeDnsResolver implements DnsResolver {
		@Override
		public InetAddress[] resolve(String host) throws UnknownHostException {
			// Return some fake DNS record for every request, we won't be using
			// it
			return new InetAddress[] { InetAddress.getByAddress(new byte[] { 1, 1, 1, 1 }) };
		}
	}

	@Test
	public void testSocketProxy() throws Exception {
		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", 80, new MyConnectionSocketFactory()));
		schreg.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		addHttpsTrustStrategy(schreg);

		DnsResolver dnsResolver = new FakeDnsResolver();
		dnsResolver = new ShuffleCacheDnsResolver();
		PoolingClientConnectionManager conman = new PoolingClientConnectionManager(schreg,dnsResolver);

		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		client.setHttpRequestRetryHandler(new SimpleHttpRequestRetryHandler());
		CookieSpecFactory csf = getCustomCookieSpecFactory();
		client.getCookieSpecs().register("easy", csf);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

		//http://sockslist.net/
		InetSocketAddress socksaddr = new InetSocketAddress("173.70.203.63", 42902);
		socksaddr = new InetSocketAddress("73.44.169.43", 34689);
		socksaddr = new InetSocketAddress("61.147.67.2", 9123);
		socksaddr = new InetSocketAddress("122.14.166.37", 1080);
		socksaddr = new InetSocketAddress("75.143.226.46", 30323);
		
		//http://www.xroxy.com/proxy-type-Socks5.htm
//		socksaddr = new InetSocketAddress("61.147.67.2", 9125);
//		socksaddr = new InetSocketAddress("124.42.127.221", 1080);
		socksaddr = new InetSocketAddress("180.153.139.246", 8888);
		String url = "http://item.yhd.com/item/102301?tp=1.0.61.0.9.Kcjj6mx";
		url = "http://1111.ip138.com/ic.asp";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", url);
		get.getParams().setParameter("socks.address", socksaddr);
		HttpResponse resp = client.execute(get);
		String html = (EntityUtils.toString(resp.getEntity(), "gbk"));
		System.out.println(html);
	}
}
