package com.lezo.rest.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.rest.QiniuBucketMacFactory;
import com.lezo.rest.baidu.pcs.PcsClient;

public class QiniuResterTest {
	String accessToken = "21.508cd049e4261c79a35e6f5ff91ae819.2592000.1428294337.4026763474-2920106";
	String rootPath = "/apps/idocs";
	QiniuRester rester = new QiniuRester();

	@Test
	public void testUpload() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
		rester.setClient(client);
		rester.setBucket("p1001");
		rester.setDomain("p1001.qiniudn.com");
		rester.setMac(QiniuBucketMacFactory.getBucketMac(rester.getBucket()).getMac());

		String targetPath = "iscript/20150210/ConfigProxyChecker/upload/ConfigProxyChecker.upload." + System.currentTimeMillis() + ".gz";
		String source = "rest.test.stt\r\n1233444565475474";
		byte[] dataBytes = source.getBytes();
		Assert.assertTrue(rester.upload(targetPath, dataBytes));
	}

	@Test
	public void testDownload() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
		rester.setClient(client);
		rester.setBucket("d1002");
		rester.setDomain("d1002.qiniudn.com");
		rester.setMac(QiniuBucketMacFactory.getBucketMac(rester.getBucket()).getMac());
		String soucePath = "iscript/20150210/ConfigProxyChecker/upload/ConfigProxyChecker.upload.1425793736885.gz";
		soucePath = "iscript/20150309/ConfigProxyChecker/5df50e4e-ad2a-4980-b72d-6eaf32231e2e/ConfigProxyChecker.20150309.1425889009119.gz";
		System.err.println("data:" + rester.download(soucePath));
	}

	@Test
	public void testLists() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
		rester.setClient(client);
		rester.setBucket("p1001");
		rester.setDomain("p1001.qiniudn.com");
		rester.setMac(QiniuBucketMacFactory.getBucketMac(rester.getBucket()).getMac());

		String targetPath = "iscript/20150210/ConfigProxyChecker/upload";
		Map<String, String> paramMap = new HashMap<String, String>();
		RestList fileList = rester.listFiles(targetPath, paramMap);
		System.err.println("data:" + fileList.getDataList().size());
	}
}
