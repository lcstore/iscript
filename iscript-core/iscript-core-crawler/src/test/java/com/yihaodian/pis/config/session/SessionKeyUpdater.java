package com.yihaodian.pis.config.session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SessionKeyUpdater {
	private static final String DEFAULT_USERAGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.68 Safari/534.24";

	public static void main(String[] args) throws Exception {
		String appConfigPath = "src/test/resources/appConfig.txt";
		String jsconfigPath = "src/test/resources/sessionConfig.txt";
		List<String> appList = FileUtils.readLines(new File(appConfigPath), "utf-8");
		List<AppConfig> appConfigs = getAppConfigs(appList);
		int total = appConfigs.size();
		int maxKeySize = 30;
		int fromAppIndex = 0;
		int maxApp4User = (total < maxKeySize) ? maxKeySize : total / 2 + (total % 2);
		List<VerifyUserConfig> userConfigs = getUserConfig();
		SessionKeyUpdater updater = new SessionKeyUpdater();
		Writer out = new FileWriter(new File(jsconfigPath));
		StringBuilder sb = new StringBuilder();
		BufferedWriter bw = new BufferedWriter(out);
		try {
			int i = 0;
			while (fromAppIndex < total) {
				VerifyUserConfig uConfig = userConfigs.get(i++);
				int toIndex = fromAppIndex + maxApp4User;
				toIndex = toIndex > total ? total : toIndex;
				List<AppConfig> subConfigs = appConfigs.subList(fromAppIndex, toIndex);
				DefaultHttpClient client = updater.createHttpClient();
				TmallLogin login = new TmallLogin();
				login.doLogin(client, uConfig.getTbUserName(), uConfig.getTbPassword());
				updater.updateSessionKey(client, uConfig, subConfigs);
				fromAppIndex = toIndex;
				for (AppConfig oConfig : subConfigs) {
					bw.append("oConfig = {};\n");
					bw.append("oConfig.secret = \"" + oConfig.getAppsecret() + "\";\n");
					bw.append("oConfig.sessionKey = \"" + oConfig.getSessionKey() + "\";\n");
					bw.append("appConfig[" + oConfig.getAppkey() + "]=oConfig;\n");

					sb.append("oConfig = {};\n");
					sb.append("oConfig.secret = \"" + oConfig.getAppsecret() + "\";\n");
					sb.append("oConfig.sessionKey = \"" + oConfig.getSessionKey() + "\";\n");
					sb.append("appConfig[" + oConfig.getAppkey() + "]=oConfig;\n");
				}
				bw.flush();
			}
			String templatePath = "src/test/resources/template-taobao-items.xml";
			String destConfigPath = "src/test/resources/my-taobao-items.xml";
			write2Config(sb, templatePath, destConfigPath);
			System.out.println("write to file:" + destConfigPath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				IOUtils.closeQuietly(bw);
			}
			if (out != null) {
				IOUtils.closeQuietly(out);
			}
		}

	}

	private static void write2Config(StringBuilder sb, String templatePath, String destConfigPath) throws IOException {
		String encoding = "utf-8";
		String tempConfig = FileUtils.readFileToString(new File(templatePath), encoding);
		String configs = tempConfig.replace("{taobao.app.configs}", sb.toString());
		FileUtils.writeStringToFile(new File(destConfigPath), configs, encoding);
	}

	private static List<AppConfig> getAppConfigs(List<String> appList) {
		List<AppConfig> configs = new ArrayList<AppConfig>(appList.size());
		for (String configString : appList) {
			String[] configArr = configString.split("\t");
			if (configArr == null || configArr.length < 2) {
				throw new RuntimeException("error appConfig:" + configString);
			}
			int i = -1;
			AppConfig config = new AppConfig();
			config.setAppkey(configArr[++i].trim());
			config.setAppsecret(configArr[++i].trim());
			config.setAppkey(config.getAppkey().trim());
			configs.add(config);
		}
		return configs;
	}

	private void updateSessionKey(DefaultHttpClient client, VerifyUserConfig uConfig, List<AppConfig> AppConfigs)
			throws Exception {
		for (AppConfig config : AppConfigs) {
			setSessionKey(client, config, uConfig);
		}
	}

	private void setSessionKey(DefaultHttpClient client, AppConfig config, VerifyUserConfig uConfig) throws Exception {
		String sessionUrl = "http://container.open.taobao.com/container?appkey=" + config.getAppkey() + "&encode=utf-8";
		HttpGet sessionGet = new HttpGet(sessionUrl);
		HttpResponse response = client.execute(sessionGet);
		String content = EntityUtils.toString(response.getEntity());
		EntityUtils.consume(response.getEntity());
		Document dom = Jsoup.parse(content);
		HttpPost autherPost = getAutherPost(dom);
		client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		response = client.execute(autherPost);
		content = EntityUtils.toString(response.getEntity());
		String verifyUrl = getVerifyUrl(content);
		if (verifyUrl != null) {
			doSecondVerify(client, uConfig, verifyUrl);
			// dom = Jsoup.parse(destContent);
			response = client.execute(autherPost);
			String getSessionUrl = response.getFirstHeader("Location").getValue();
			String key = "top_session";
			String value = URLUtils.getParamMap(getSessionUrl).get(key);
			config.setSessionKey(value);
			EntityUtils.consume(response.getEntity());
		} else {
			System.out.println("not found verifyUrl:" + content);
			String getSessionUrl = response.getFirstHeader("Location").getValue();
			String key = "top_session";
			String value = URLUtils.getParamMap(getSessionUrl).get(key);
			config.setSessionKey(value);
			EntityUtils.consume(response.getEntity());
		}

	}

	private String getVerifyUrl(String content) throws Exception {
		String find = "二次验证";
		int index = content.indexOf(find);
		if (index > -1) {
			int start = content.lastIndexOf("{", index);
			int end = content.indexOf("}", index);
			String sArgs = content.substring(start, end + 1);
			JSONObject jObj = new JSONObject(sArgs);
			return jObj.getString("url");
		}
		return null;
	}

	private String doSecondVerify(HttpClient client, VerifyUserConfig userConfig, String verifyUrl) throws Exception {
		HttpUriRequest loginGet = new HttpGet(verifyUrl);
		HttpResponse response = client.execute(loginGet);
		String content = EntityUtils.toString(response.getEntity());
		EntityUtils.consume(response.getEntity());
		String pattern = "sendcode.*?email";
		Pattern emailReg = Pattern.compile(pattern);
		Matcher matcher = emailReg.matcher(content);
		System.out.println("doSecondVerify:" + content);
		if (matcher.find()) {
			Document dom = Jsoup.parse(content, "https://aq.taobao.com/durex/");
			Elements urlEls = dom.select("#J_Email");
			String checkcodeUrl = urlEls.first().absUrl("action");
			String codeEmalUrl = "https://aq.taobao.com/durex/" + matcher.group();
			System.out.println("codeEmalUrl:" + codeEmalUrl);
			System.out.println("checkcodeUrl:" + checkcodeUrl);
			HttpUriRequest sendEmailGet = new HttpGet(codeEmalUrl);
			response = client.execute(sendEmailGet);
			content = EntityUtils.toString(response.getEntity());
			EntityUtils.consume(response.getEntity());
			// XXX wait for email to send
			TimeUnit.SECONDS.sleep(5);

			System.out.println("sendEmailGet:" + content);
			EmailVerifyCodeFinder emailVerify = new EmailVerifyCodeFinder();
			String vCode = emailVerify.getVerifyCode(userConfig.getEmailAddr(), userConfig.getEmailPwd());
			System.out.println("vCode:" + vCode);

			HttpPost next = new HttpPost(checkcodeUrl);
			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			pairList.add(new BasicNameValuePair("checkCode", vCode));
			pairList.add(new BasicNameValuePair("checkType", "email"));
			HttpEntity postEntity = new UrlEncodedFormEntity(pairList, "utf-8");
			next.setEntity(postEntity);

			response = client.execute(next);
			content = EntityUtils.toString(response.getEntity());
			EntityUtils.consume(response.getEntity());
			System.out.println("SessionGet:" + content);

		}
		return content;
	}

	private HttpPost getAutherPost(Document dom) throws IOException {
		// http://container.api.taobao.com/container
		// agreement true 14
		// agreementsign
		// 21622138-22994778-1817804237-504FF182BDC132D39E9FC601CDE1ED44 75
		// appkey 21622138 15
		// encode utf-8 12
		// sign 08rXO/RghTOMp/gRiRKaqA== 37
		// timestamp 2013-09-20 22:18:06 33
		String basicUrl = "http://container.api.taobao.com/container";
		List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat();
		addNameValue(dom, "sign", nvPairs);
		addNameValue(dom, "encode", nvPairs);
		addNameValue(dom, "appkey", nvPairs);
		addNameValue(dom, "agreementsign", nvPairs);
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		String sNow = sdf.format(now);
		nvPairs.add(new BasicNameValuePair("timestamp", sNow));
		nvPairs.add(new BasicNameValuePair("agreement", "true"));
		HttpPost requset = new HttpPost(basicUrl);
		requset.setEntity(new UrlEncodedFormEntity(nvPairs, "UTF-8"));
		return requset;
	}

	public static void addNameValue(Document dom, String name, List<NameValuePair> nvPairs) {
		String query = "#" + name;
		Elements paramAs = dom.select(query);
		if (paramAs.isEmpty()) {
			System.err.println("not found:" + name);
		} else {
			nvPairs.add(new BasicNameValuePair(name, paramAs.first().attr("value")));
		}
	}

	public static List<VerifyUserConfig> getUserConfig() {
		List<VerifyUserConfig> configList = new ArrayList<VerifyUserConfig>();
		VerifyUserConfig config = new VerifyUserConfig();
		config.setTbUserName("pis_1001");
		config.setTbPassword("pis1234");
		config.setEmailAddr("pis1001@163.com");
		config.setEmailPwd("pis1234");
		configList.add(config);
		config = new VerifyUserConfig();
		config.setTbUserName("pis1002");
		config.setTbPassword("pis1234");
		config.setEmailAddr("pis1002@163.com");
		config.setEmailPwd("pis1234");
		configList.add(config);
		config = new VerifyUserConfig();
		config.setTbUserName("pis1003");
		config.setTbPassword("pis1234");
		config.setEmailAddr("pis1003@163.com");
		config.setEmailPwd("pis1234");
		configList.add(config);
		return configList;
	}

	private DefaultHttpClient createHttpClient() {
		ClientConnectionManager conman = createClientConnManager();
		HttpParams params = createHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(conman, params);
//		 return client;
		return newClient();
	}

	private ClientConnectionManager createClientConnManager() {
		SSLSocketFactory ssf = getSSLSocket();
		ssf = SSLSocketFactory.getSocketFactory();
		SchemeRegistry schemeRegister = new SchemeRegistry();
		schemeRegister.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegister.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		schemeRegister.register(new Scheme("https", 443, ssf));
		ThreadSafeClientConnManager tsconnectionManager = new ThreadSafeClientConnManager(schemeRegister);
		return tsconnectionManager;
	}

	private SSLSocketFactory getSSLSocket() {
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return ssf;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private HttpParams createHttpParams() {
		HttpParams _params = new BasicHttpParams();
		HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(_params, "utf-8");
		HttpProtocolParams.setUseExpectContinue(_params, true);
		// config for user agent
		HttpProtocolParams.setUserAgent(_params, DEFAULT_USERAGENT);
		HttpClientParams.setCookiePolicy(_params, CookiePolicy.BROWSER_COMPATIBILITY);
		// set timeout
		HttpConnectionParams.setConnectionTimeout(_params, 120000);
		HttpConnectionParams.setSoTimeout(_params, 120000);
		return _params;
	}

	public static DefaultHttpClient newClient() {
		HttpParams _params = new BasicHttpParams();
		// 增加最大连接到200
		ConnManagerParams.setMaxTotalConnections(_params, 200);
		// 增加每个路由的默认最大连接到20
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
		// 对localhost:80增加最大连接到50
		HttpHost localhost = new HttpHost("locahost", 80);
		connPerRoute.setMaxForRoute(new HttpRoute(localhost), 50);
		ConnManagerParams.setMaxConnectionsPerRoute(_params, connPerRoute);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);

		HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(_params, "utf-8");
		HttpProtocolParams.setUseExpectContinue(_params, true);
		// config for user agent
		HttpProtocolParams.setUserAgent(_params, DEFAULT_USERAGENT);
		HttpClientParams.setCookiePolicy(_params, CookiePolicy.BROWSER_COMPATIBILITY);
		DefaultHttpClient client = new DefaultHttpClient(cm, _params);
		return client;
	}
}
