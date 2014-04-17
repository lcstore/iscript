package com.lezo.iscript.yeam.config;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.lezo.iscript.crawler.http.GzipHttpRequestInterceptor;
import com.lezo.iscript.crawler.http.GzipHttpResponseInterceptor;
import com.lezo.iscript.crawler.http.HttpParamsConstant;
import com.lezo.iscript.crawler.http.UserAgentManager;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class HuihuiSigner implements ConfigParser {

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
		HttpEntity postEntity = getPostEntity(username, password);
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
		return html;
	}

	public static DefaultHttpClient createHttpClient() {
		ClientConnectionManager conman = createClientConnManager();
		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
		// client.setHttpRequestRetryHandler(new
		// SimpleHttpRequestRetryHandler());
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
