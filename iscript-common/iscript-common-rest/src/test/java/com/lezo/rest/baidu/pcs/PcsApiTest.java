package com.lezo.rest.baidu.pcs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSListInfoResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSSimplefiedResponse;
import com.baidu.pcs.BaiduPCSClient;

public class PcsApiTest {
	// String accessToken =
	// "23.5a2e8807af73c68012a4eb73efb01e04.2592000.1428292712.4026763474-2920106";
	String accessToken = "21.508cd049e4261c79a35e6f5ff91ae819.2592000.1428294337.4026763474-2920106";
	BaiduPCSClient client = new BaiduPCSClient(accessToken);

	@Test
	public void testList() {
		String path = "/apps/idocs";
		String by = null;
		String order = null;
		PCSListInfoResponse res = client.list(path, by, order);
		System.err.println(res.status.message);
		System.err.println(res.list.size());
	}

	@Test
	public void testRestList() throws Exception {
		DefaultHttpClient hc = new DefaultHttpClient(PcsClient.createClientConnManager());
		String path = "/apps/idocs";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("method", "list"));
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("path", path));
		params.add(new BasicNameValuePair("limit", "2-3"));

		String by = "time";
		String order = "desc";
		if (StringUtils.isNotEmpty(by)) {
			params.add(new BasicNameValuePair("by", by));
		}

		if (StringUtils.isNotEmpty(order)) {
			params.add(new BasicNameValuePair("order", order));
		}

		String url = "https://pcs.baidu.com/rest/2.0/pcs/file?" + buildParams(params);
		HttpGet request = new HttpGet(url);

		HttpResponse respone = hc.execute(request);
		System.out.println("down:" + EntityUtils.toString(respone.getEntity(), "UTF-8"));
	}

	@Test
	public void testMakeDir() {
		String path = "/apps/idocs";
		PCSFileInfoResponse res = client.makeDir(path + "/testDir/");
		System.err.println(res.status.message);
		System.err.println(res.commonFileInfo.path);
	}

	@Test
	public void testUpload() {
		String path = "/apps/idocs";
		String source = "src/main/resources/file.temp";
		String target = path + "/testDir/file.temp";
		PCSFileInfoResponse res = client.uploadFile(source, target);
		System.err.println(res.status.message);
		System.err.println(res.commonFileInfo.path);
	}

	@Test
	public void testDownload() {
		String path = "/apps/idocs";
		String target = "src/main/resources/down.file.temp";
		String source = path + "/testDir/file.temp";
		PCSSimplefiedResponse res = client.downloadFile(source, target);
		System.err.println(res.message);
		System.err.println(new File(target).exists());
	}

	@Test
	public void testRestDownload() throws Exception {
		DefaultHttpClient hc = new DefaultHttpClient(PcsClient.createClientConnManager());
		String path = "/apps/idocs";
		String source = path + "/testDir/file.temp";

		String method = "download";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("method", method));
		params.add(new BasicNameValuePair("access_token", accessToken));
		params.add(new BasicNameValuePair("path", source));

		String command = "file";
		String url = "https://pcs.baidu.com/rest/2.0/pcs/" + command + "?" + buildParams(params);
		HttpGet request = new HttpGet(url);

		HttpResponse respone = hc.execute(request);
		System.out.println("down:" + EntityUtils.toString(respone.getEntity(), "UTF-8"));
	}

	@Test
	public void testDelete() {
		String path = "/apps/idocs";
		String source = path + "/testDir/file.temp";
		PCSSimplefiedResponse res = client.deleteFile(source);
		System.err.println(res.message);
	}

	@Test
	public void testDeleteFolder() {
		String path = "/apps/idocs";
		String source = path + "/testDir/";
		PCSSimplefiedResponse res = client.deleteFile(source);
		System.err.println(res.message);
	}

	@Test
	public void testRestUpload() throws Exception {
		DefaultHttpClient hc = new DefaultHttpClient(PcsClient.createClientConnManager());
		String path = "/apps/idocs";
		String target = path + "/testDir/file.temp.01";
		String source = "asdstjekwtjewp,wel234392380523409";

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("method", "upload"));
		paramList.add(new BasicNameValuePair("access_token", accessToken));
		paramList.add(new BasicNameValuePair("path", target));
		paramList.add(new BasicNameValuePair("ondup", "overwrite"));

		String url = "https://pcs.baidu.com/rest/2.0/pcs/file?" + buildParams(paramList);

		int index = target.lastIndexOf("/");
		String suffix = target.substring(index + 1, target.length());

		HttpPost post = new HttpPost(url);
		MultipartEntity entity = new MultipartEntity();
		ContentBody bsData = new ByteArrayBody(source.getBytes(), suffix);
		entity.addPart("uploadedfile", bsData);

		post.setEntity(entity);

		HttpResponse httpresponse = hc.execute(post);
		System.out.println("dfd:" + EntityUtils.toString(httpresponse.getEntity()));
	}

	private String buildParams(List<NameValuePair> paramList) {
		String ret = null;
		try {
			HttpEntity paramsEntity = new UrlEncodedFormEntity(paramList, "utf8");
			ret = EntityUtils.toString(paramsEntity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
