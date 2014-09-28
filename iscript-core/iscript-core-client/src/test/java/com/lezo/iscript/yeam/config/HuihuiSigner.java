package com.lezo.iscript.yeam.config;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lezo.iscript.crawler.http.GzipHttpRequestInterceptor;
import com.lezo.iscript.crawler.http.GzipHttpResponseInterceptor;
import com.lezo.iscript.crawler.http.HttpParamsConstant;
import com.lezo.iscript.crawler.http.SimpleHttpRequestRetryHandler;
import com.lezo.iscript.crawler.http.UserAgentManager;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class HuihuiSigner implements ConfigParser {

	private String getLocationUrl(Document dom) {
		Elements elements = dom.select("script[language=JavaScript]");

		for (int i = 0; i < elements.size(); i++) {
			String sHtml = elements.get(i).html();
			String getUrl = getLocationUrl(sHtml);
			if (getUrl != null) {
				return getUrl;
			}
		}
		return null;
	}

	private String getLocationUrl(String sHtml) {
		Pattern oReg = Pattern.compile("http://reg.huihui.cn/crossdomain.jsp.*?loginCookie.*?login");
		Matcher matcher = oReg.matcher(sHtml);
		if (matcher.find()) {
			return matcher.group();
		}
		oReg = Pattern.compile("http://www.huihui.cn/activate.*?username.*?login");
		matcher = oReg.matcher(sHtml);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	private HttpEntity getPostEntity(String username, String password) throws Exception {
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

	@Override
	public String getName() {
		return "huihui-sign";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		String username = (String) task.get("user");
		String password = (String) task.get("pwd");
		String postUrl = "https://reg.163.com/logins.jsp";
		DefaultHttpClient client = createHttpClient();

		HttpPost post = new HttpPost();
		post.setURI(new URI(postUrl));
		post.addHeader("Referer", "http://www.huihui.cn/login");
		HttpEntity postEntity = getPostEntity(username, password);
		post.setEntity(postEntity);
		HttpResponse res = client.execute(post);
		String html = EntityUtils.toString(res.getEntity(), "UTF-8");
		post.abort();
		Document dom = Jsoup.parse(html, postUrl);
		String getUrl = getLocationUrl(dom);
		if (getUrl == null) {
			Elements oInfoAs = dom.select("#eHint.info");
			if (!oInfoAs.isEmpty()) {
				return oInfoAs.text();
			}
		}
		HttpGet get = new HttpGet(getUrl);
		res = client.execute(get);
		html = EntityUtils.toString(res.getEntity(), "UTF-8");
		get.abort();
		dom = Jsoup.parse(html, getUrl);
		getUrl = getLocationUrl(dom);
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
		JSONObject rsObject = new JSONObject();
		rsObject.put("rs", html);
		task.getArgs().remove("pwd");
		rsObject.put("args", new JSONObject(task.getArgs()));
		return rsObject.toString();
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

	public static ClientConnectionManager createClientConnManager() {
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		supportedSchemes.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		addHttpsTrustStrategy(supportedSchemes);
		// addHttpsTrustManager(supportedSchemes);
		ThreadSafeClientConnManager tsconnectionManager = new ThreadSafeClientConnManager(supportedSchemes);
		tsconnectionManager.setMaxTotal(HttpParamsConstant.MAX_TOTAL_CONNECTIONS);
		return tsconnectionManager;
	}

	private static void addHttpsTrustManager(SchemeRegistry supportedSchemes) {
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			// SSLContext sslcontext = SSLContext.getInstance("SSL");
			TrustManager[] tm = new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
					// return new X509Certificate[] {};
				}
			} };
			SecureRandom random = new java.security.SecureRandom();
			sslcontext.init(null, tm, random);
			SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
			supportedSchemes.register(new Scheme("https", 443, sf));
			// supportedSchemes.register(new Scheme("https", 8443, sf));
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		// HttpClientParams.setCookiePolicy(_params,
		// CookiePolicy.BROWSER_COMPATIBILITY);

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
