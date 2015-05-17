package com.lezo.rest.baidu.pcs;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.lezo.iscript.rest.http.HttpClientUtils;

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
		DefaultHttpClient hc = HttpClientUtils.createHttpClient();
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
		DefaultHttpClient hc = HttpClientUtils.createHttpClient();
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
		DefaultHttpClient hc = HttpClientUtils.createHttpClient();
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
		DefaultHttpClient hc = HttpClientUtils.createHttpClient();
		String params = EntityUtils.toString(new UrlEncodedFormEntity(paramList));
		httpget.setURI(new URI(httpget.getURI().toString() + "?" + params));
		HttpResponse httpresponse = hc.execute(httpget);
		HttpEntity entity = httpresponse.getEntity();
		System.out.println("dfd:" + EntityUtils.toString(entity));
	}

}
