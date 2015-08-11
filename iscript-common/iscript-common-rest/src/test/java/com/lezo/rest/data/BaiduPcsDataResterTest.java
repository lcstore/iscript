package com.lezo.rest.data;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.rest.http.HttpClientUtils;

public class BaiduPcsDataResterTest {
	// String bucket = "istore_doc";
	// String accessToken =
	// "21.ce27b3198b39d4c0b3664d75050d7a40.2592000.1439881772.4026763474-1856205";
	String bucket = "idocs";
	String accessToken =
			"21.dcaf9aca78a0dcb555ff2e76ccf3b21d.2592000.1440400567.4026763474-2920106";
	String rootPath = "/apps/" + bucket;
	BaiduPcsRester rester = new BaiduPcsRester();

	// @Test
	public void testUpload() throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		rester.setClient(client);
		rester.setBucket(bucket);
		rester.setAccessToken(accessToken);
		String targetPath = "/iscript/1001.gz";
		String source = "rest.我们.stt";
		byte[] dataBytes = source.getBytes();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(bos);
		gos.write(dataBytes);
		gos.flush();
		gos.close();
		dataBytes = bos.toByteArray();
		Assert.assertTrue(rester.upload(targetPath, dataBytes));
	}

	@Test
	public void testDownload() throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		rester.setClient(client);
		rester.setBucket(bucket);
		rester.setAccessToken(accessToken);
		String path = "/apps/idocs";
		path = "";
		String soucePath = path + "/rest/rest1001.txt";
		soucePath = "iscript/20150309/ConfigProxyChecker/5df50e4e-ad2a-4980-b72d-6eaf32231e2e/ConfigProxyChecker.20150309.1425890407643.gz";
		// soucePath =
		// "/apps/istore_doc/iscript/20150618/ConfigProxyDetector/642fd595-3499-4797-aed3-3edc9352b83f/ConfigProxyDetector.20150618.1434558506185.gz";
		System.err.println("data:" + rester.download(soucePath));
	}

	@Test
	public void testLists() throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		rester.setClient(client);
		rester.setBucket(bucket);
		rester.setAccessToken(accessToken);
		String path = "/apps/" + bucket;
		String targetPath = "/apps/istore_doc/iscript/20150504/ConfigProxyDetector/8b13bf37-f6a4-43b0-ae25-ffb396248392";
		targetPath = "/apps/idocs/iscript/20150509/ConfigProxyDetector/8f84c4ea-f64c-41b3-a4be-b84728b248a2";
		targetPath = "/apps/idocs/iscript/20150518/ConfigProxySeedHandler/68d5807a-4ad1-485e-90f5-bc99d7645274";
		targetPath = "/apps/idocs/iscript/20150518/ConfigProxyDetector/5";
		targetPath = "/apps/idocs/iscript/20150526/ConfigTmallBrandShop/4c17ebd2-ae53-4418-b6a4-f4963b569c8a";
		// targetPath =
		// "/apps/istore_doc/iscript/20150618/ConfigProxyDetector/642fd595-3499-4797-aed3-3edc9352b83f";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("limit", "0-100");
		RestList fileList = rester.listFiles(targetPath, paramMap);
		for (RestFile dFile : fileList.getDataList()) {
			System.err.println(dFile.getPath() + ":" + dFile.getUpdateTime() + ":" + dFile.getCreateTime());
		}
		System.err.println("size:" + fileList.getDataList().size() + ",marker:" + fileList.getMarker());
	}

	@Test
	public void testGetStamp() {
		String path = "18/ConfigProxyDetector/5/ConfigProxyDetector.20150518.1431888580228.gz";
		int toIndex = path.lastIndexOf(".");
		int fromIndex = path.lastIndexOf(".", toIndex - 1);
		System.out.println(fromIndex + "," + toIndex + ":" + path.substring(fromIndex + 1, toIndex));
	}
}
