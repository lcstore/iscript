package com.lezo.iscript.yeam.http;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.params.ConnRoutePNames;
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
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.lezo.iscript.proxy.ProxyClientUtils;
import com.lezo.iscript.rest.http.HttpParamsConstant;
import com.lezo.iscript.rest.http.ProxySocketFactory;
import com.lezo.iscript.rest.http.ShuffleCacheDnsResolver;
import com.lezo.iscript.rest.http.SimpleHttpRequestRetryHandler;
import com.lezo.iscript.rest.http.UserAgentManager;

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
		// schreg.register(new Scheme("http", 80, new
		// MyConnectionSocketFactory()));
		schreg.register(new Scheme("http", 80, ProxySocketFactory.getSocketFactory()));
		schreg.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		addHttpsTrustStrategy(schreg);

		DnsResolver dnsResolver = new FakeDnsResolver();
		dnsResolver = new ShuffleCacheDnsResolver();
		PoolingClientConnectionManager conman = new PoolingClientConnectionManager(schreg, dnsResolver);

		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		client.setHttpRequestRetryHandler(new SimpleHttpRequestRetryHandler());
		CookieSpecFactory csf = getCustomCookieSpecFactory();
		client.getCookieSpecs().register("easy", csf);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

		// http://sockslist.net/
		InetSocketAddress socksaddr = new InetSocketAddress("173.70.203.63", 42902);
		socksaddr = new InetSocketAddress("73.44.169.43", 34689);
		socksaddr = new InetSocketAddress("61.147.67.2", 9123);
		socksaddr = new InetSocketAddress("122.14.166.37", 1080);

		socksaddr = new InetSocketAddress("76.233.192.215", 31258);

		// http://fastproxyservers.org/socks5-servers.htm
		socksaddr = new InetSocketAddress("202.38.95.66", 1080);
		socksaddr = new InetSocketAddress("207.255.105.53", 36006);

		socksaddr = new InetSocketAddress("115.112.189.79", 8080);
		socksaddr = new InetSocketAddress("37.228.134.52", 7808);
		socksaddr = new InetSocketAddress("110.139.128.4", 80);
		socksaddr = new InetSocketAddress("209.141.9.201", 1080);
		socksaddr = new InetSocketAddress("103.246.161.194", 1080);
		socksaddr = new InetSocketAddress("1.179.143.178", 3128);
		socksaddr = new InetSocketAddress("117.169.1.100", 4203);

		// http://www.xroxy.com/proxy-type-Socks5.htm
		// socksaddr = new InetSocketAddress("61.147.67.2", 9125);
		// socksaddr = new InetSocketAddress("124.42.127.221", 1080);
		socksaddr = new InetSocketAddress("202.197.127.139", 1080);
		String url = "http://item.yhd.com/item/102301?tp=1.0.61.0.9.Kcjj6mx";
		url = "http://1111.ip138.com/ic.asp";
		// url = "http://www.baidu.com/";
		HttpGet get = new HttpGet(url);
		get.addHeader("Referer", url);
		get.getParams().setParameter(ProxySocketFactory.SOCKET_PROXY, new Proxy(Proxy.Type.SOCKS, socksaddr));
		// get.getParams().setParameter(ProxySocketFactory.SOCKET_PROXY, new
		// Proxy(Proxy.Type.SOCKS,
		// InetSocketAddress.createUnresolved("79.127.124.115", 1080)));
		HttpResponse resp = client.execute(get);
		String html = (EntityUtils.toString(resp.getEntity(), "gbk"));
		System.out.println(html);
	}

	@Test
	public void testSslProxy() throws Exception {
		URI uri = new URI("http://1111.ip138.com/ic.asp");
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("107.182.17.243", 7808, "http"));
		client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, SSLSocketFactory.getSystemSocketFactory()));

		HttpUriRequest request = new HttpGet(uri);
		HttpResponse resp = client.execute(request);
		String html = (EntityUtils.toString(resp.getEntity(), "gbk"));
		System.out.println(html);
	}

	@Test
	public void testHttpProxy() throws Exception {
//		URI uri = new URI("http://1111.ip138.com/ic.asp");
		URI uri = new URI("http://httpbin.org/ip");
		DefaultHttpClient client = new DefaultHttpClient();
		// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new
		// HttpHost("107.182.17.243", 7808, "http"));
		client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, SSLSocketFactory.getSystemSocketFactory()));

		HttpUriRequest request = new HttpGet(uri);
		// request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new
		// HttpHost("107.182.17.243", 7808, "http"));
		request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("183.250.179.29", 80, "http"));
		request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("202.101.96.154", 8888, "http"));
		request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("222.188.10.1", 1080, "http"));
		HttpResponse resp = client.execute(request);
		String html = (EntityUtils.toString(resp.getEntity(), "gbk"));
		System.out.println(html);
	}

	@Test
	public void testChooseProxy() throws Exception {
		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", 80, ProxySocketFactory.getSocketFactory()));
		schreg.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		addHttpsTrustStrategy(schreg);

		DnsResolver dnsResolver = new FakeDnsResolver();
		dnsResolver = new ShuffleCacheDnsResolver();
		PoolingClientConnectionManager conman = new PoolingClientConnectionManager(schreg, dnsResolver);

		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		client.setHttpRequestRetryHandler(new SimpleHttpRequestRetryHandler());
		CookieSpecFactory csf = getCustomCookieSpecFactory();
		client.getCookieSpecs().register("easy", csf);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

		String url = "http://1111.ip138.com/ic.asp";

		HttpUriRequest request = null;
		// http
		request = ProxyClientUtils.createHttpGet(url, "117.177.243.35", 80, 1);
		request = ProxyClientUtils.createHttpGet(url, "183.216.164.240", 8123, 1);
		// socket
		request = ProxyClientUtils.createHttpGet(url, "202.197.127.139", 1080, 2);
		HttpResponse resp = client.execute(request);
		String html = (EntityUtils.toString(resp.getEntity(), "gbk"));
		System.out.println(html);
	}
}
