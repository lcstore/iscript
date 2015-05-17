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
	// String accessToken =
	// "21.2c338df374df1340034fb1ac1ecb9d66.2592000.1428417297.4026763474-2920106";
	// private String bucket = "idocs";
	// String accessToken =
	// "21.36060ec14d4d658968991d26953c922f.2592000.1428571671.4026763474-1856205";
	// private String bucket = "istore_doc";
//	String accessToken = "21.bbab445326661b526361f507d1a7305d.2592000.1433652624.4026763474-1856205";
	String accessToken = "21.ad2f5fb25bfaea4bea82efc054ba9b45.2592000.1433653314.4026763474-2920106";
	private String bucket = "idocs";
	String rootPath = "/apps/" + bucket;
	BaiduPcsRester rester = new BaiduPcsRester();

//	@Test
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
		soucePath = "file.temp.01";
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
//		targetPath = "/apps/idocs/iscript/20150506/ConfigProxySeedHandler/1b358cc0-a04b-4ba4-9048-20391a72c8fe";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("limit", "30-40");
		RestList fileList = rester.listFiles(targetPath, paramMap);
		for (RestFile dFile : fileList.getDataList()) {
			System.err.println(dFile.getPath() + ":" + dFile.getUpdateTime());
		}
		System.err.println("size:" + fileList.getDataList().size() + ",marker:" + fileList.getMarker());
	}
}
