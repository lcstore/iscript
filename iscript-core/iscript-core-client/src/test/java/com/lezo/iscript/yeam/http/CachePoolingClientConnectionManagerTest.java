package com.lezo.iscript.yeam.http;

import java.net.InetAddress;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.InMemoryDnsResolver;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.junit.Test;

import com.lezo.iscript.crawler.http.HttpClientUtils;
import com.lezo.iscript.crawler.http.HttpParamsConstant;
import com.lezo.iscript.crawler.http.UserAgentManager;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年12月4日
 */
public class CachePoolingClientConnectionManagerTest {

	@Test
	public void test() throws Exception {
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		supportedSchemes.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		addHttpsTrustStrategy(supportedSchemes);

		InMemoryDnsResolver dnsResolver = new InMemoryDnsResolver();
		CachePoolingClientConnectionManager tsconnectionManager = new CachePoolingClientConnectionManager(supportedSchemes, dnsResolver);

		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(tsconnectionManager, params);
		String host = "www.baidu.com";
		InetAddress[] inetArr = InetAddress.getAllByName(host);
		dnsResolver.add(host, inetArr);
		HttpGet get = new HttpGet("http://www.baidu.com/");
		String html = HttpClientUtils.getContent(client, get);
		get = new HttpGet("http://www.baidu.com/");
		html = HttpClientUtils.getContent(client, get);
		System.err.println(html);
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
			SSLSocketFactory sf = new SSLSocketFactory(acceptingTrustStrategy,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			supportedSchemes.register(new Scheme("https", 443, sf));
			// supportedSchemes.register(new Scheme("https", 8443, sf));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
