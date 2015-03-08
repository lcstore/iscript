package com.lezo.rest.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.rest.baidu.pcs.PcsClient;

public class BaiduPcsDataResterTest {
	String accessToken = "21.508cd049e4261c79a35e6f5ff91ae819.2592000.1428294337.4026763474-2920106";
	String rootPath = "/apps/idocs";
	BaiduPcsRester rester = new BaiduPcsRester();

	@Test
	public void testUpload() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
		rester.setClient(client);
		rester.setBucket("idocs");
		rester.setAccessToken(accessToken);
		String targetPath = "/iscript/20150308/rest/rest1001.txt";
		String source = "rest.test.stt";
		byte[] dataBytes = source.getBytes();
		Assert.assertTrue(rester.upload(targetPath, dataBytes));
	}

	@Test
	public void testDownload() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
		rester.setClient(client);
		rester.setBucket("idocs");
		rester.setAccessToken(accessToken);
		String path = "/apps/idocs";
		path = "";
		String soucePath = path + "/rest/rest1001.txt";
		System.err.println("data:" + rester.download(soucePath));
	}

	@Test
	public void testLists() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
		rester.setClient(client);
		rester.setBucket("idocs");
		rester.setAccessToken(accessToken);
		String path = "/apps/idocs";
		String targetPath = path + "/rest/";
		Map<String, String> paramMap = new HashMap<String, String>();
		RestFileList fileList = rester.listFiles(path, paramMap);
		System.err.println("data:" + fileList.getDataList().size());
	}
}
