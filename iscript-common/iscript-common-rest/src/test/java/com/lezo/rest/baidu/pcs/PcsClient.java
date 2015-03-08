package com.lezo.rest.baidu.pcs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
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
import org.junit.Test;

import com.lezo.http.HttpParamsConstant;
import com.lezo.rest.baidu.pcs.MySSLSocketFactory;

public class PcsClient {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// String access_token =
		// "3.e499588ba0c670981ea43961628db6a2.2592000.1389015994.4026763474-1552221";
		// String url =
		// "https://pcs.baidu.com/rest/2.0/pcs/quota?method=info&access_token="
		// + access_token;
		// // Get请求
		// HttpGet httpget = new HttpGet(url);
		// List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		// DefaultHttpClient hc = new DefaultHttpClient();
		// String params = EntityUtils.toString(new
		// UrlEncodedFormEntity(paramList));
		// httpget.setURI(new URI(httpget.getURI().toString() + "?" + params));
		// HttpResponse httpresponse = hc.execute(httpget);
		// HttpEntity entity = httpresponse.getEntity();
		// System.out.println(EntityUtils.toString(entity));

		String access_token = "3.a1333cd5eebc4a402e706e06b060b60a.2592000.1389019338.4026763474-1552221";
		String url = "https://pcs.baidu.com/rest/2.0/pcs/file";
		String path = "/apps/emao_doc/mydi";
		// Get请求
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("method", "mkdir"));
		paramList.add(new BasicNameValuePair("access_token", access_token));
		paramList.add(new BasicNameValuePair("path", path));
		DefaultHttpClient hc = makeHttpClient();
		httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
		HttpResponse httpresponse = hc.execute(httpPost);
		HttpEntity entity = httpresponse.getEntity();
		System.out.println("dfd:" + EntityUtils.toString(entity));
	}

	@Test
	public void uploadFile() throws Exception {
		String access_token = "21.fcc31ac0a79532ae080e3b26e191b55b.2592000.1405140469.4026763474-2920106";
		String url = "https://c.pcs.baidu.com/rest/2.0/pcs/file";
		String path = "emao_doc/myRegion.txt";
		String source = FileUtils.readFileToString(new File("src/main/resources/file.temp"));
		// Get请求
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("method", "upload"));
		paramList.add(new BasicNameValuePair("access_token", access_token));
		paramList.add(new BasicNameValuePair("path", path));
		paramList.add(new BasicNameValuePair("file", source));
		paramList.add(new BasicNameValuePair("ondup", "overwrite"));
		DefaultHttpClient hc = new DefaultHttpClient(createClientConnManager());
		httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
		HttpResponse httpresponse = hc.execute(httpPost);
		HttpEntity entity = httpresponse.getEntity();
		System.out.println("dfd:" + EntityUtils.toString(entity));
	}

	@Test
	public void mkdir() throws Exception {
		String access_token = "23.e6a365f2f4369e60eeedfdc9d141a591.2592000.1418106087.4026763474-2920106";
		String url = "https://pcs.baidu.com/rest/2.0/pcs/file";
		String path = "/apps/idoc/mydi";
		// Get请求
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("method", "mkdir"));
		paramList.add(new BasicNameValuePair("access_token", access_token));
		paramList.add(new BasicNameValuePair("path", path));
		DefaultHttpClient hc = makeHttpClient();
		httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
		HttpResponse httpresponse = hc.execute(httpPost);
		HttpEntity entity = httpresponse.getEntity();
		System.out.println("dfd:" + EntityUtils.toString(entity));
	}

	@Test
	public void meta() throws Exception {
		String access_token = "3.a1333cd5eebc4a402e706e06b060b60a.2592000.1389019338.4026763474-1552221";
		String url = "https://pcs.baidu.com/rest/2.0/pcs/file";
		String path = "/apps/emao_doc/outweb.txt";
		// Get请求
		HttpGet httpget = new HttpGet(url);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("method", "meta"));
		paramList.add(new BasicNameValuePair("access_token", access_token));
		paramList.add(new BasicNameValuePair("path", path));
		DefaultHttpClient hc = new DefaultHttpClient(createClientConnManager());
		String params = EntityUtils.toString(new UrlEncodedFormEntity(paramList));
		httpget.setURI(new URI(httpget.getURI().toString() + "?" + params));
		HttpResponse httpresponse = hc.execute(httpget);
		HttpEntity entity = httpresponse.getEntity();
		System.out.println("dfd:" + EntityUtils.toString(entity));
	}

	public static ClientConnectionManager createClientConnManager() throws Exception {
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		supportedSchemes.register(new Scheme("ftp", 21, PlainSocketFactory.getSocketFactory()));
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		supportedSchemes.register(new Scheme("https", 443, ssf));

		ThreadSafeClientConnManager tsconnectionManager = new ThreadSafeClientConnManager(supportedSchemes);
		tsconnectionManager.setMaxTotal(HttpParamsConstant.CCM_MAX_TOTAL);
		return tsconnectionManager;
	}

	public static DefaultHttpClient makeHttpClient() {
		DefaultHttpClient client = null;

		HttpParams connParams = new BasicHttpParams();
		connParams.setParameter(ConnManagerParams.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRoute() {
			public int getMaxForRoute(HttpRoute route) {
				return 6;
			}
		});
		connParams.setParameter(ConnManagerParams.MAX_TOTAL_CONNECTIONS, 20);
		ConnManagerParams.setMaxTotalConnections(connParams, 20);
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			org.apache.http.conn.ssl.SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", sf, 443));

			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(connParams, schemeRegistry);
			cm.setMaxTotal(20);

			HttpParams httpParams = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);

			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, "UTF-8");

			HttpProtocolParams.setUserAgent(httpParams, "PCS_3rdApp");

			client = new DefaultHttpClient(cm, httpParams);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}

		return client;
	}
}
