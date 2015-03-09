package com.lezo.rest.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.rest.QiniuBucketMacFactory;
import com.lezo.rest.baidu.pcs.PcsClient;

public class QiniuResterTest {
	private static final String CHARSET_NAME = "UTF-8";
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
	public void testUploadLines() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
		rester.setClient(client);
		rester.setBucket("p1001");
		rester.setDomain("p1001.qiniudn.com");
		rester.setMac(QiniuBucketMacFactory.getBucketMac(rester.getBucket()).getMac());

		String targetPath = "iscript/20150210/ConfigProxyChecker/upload/ConfigProxyChecker.upload.7.gz";
		List<String> lineList = FileUtils.readLines(new File("src/main/resources/down.file.temp"), "UTF-8");
		byte[] dataBytes = toGzipByteArray(lineList);
		Assert.assertTrue(rester.upload(targetPath, dataBytes));
	}

	private byte[] toGzipByteArray(List<String> rWritables) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(bos);
			for (String result : rWritables) {
				result += "\n";
				gzos.write(result.getBytes(CHARSET_NAME));
			}
			gzos.close();
			return bos.toByteArray();
		} finally {
			IOUtils.closeQuietly(bos);
		}
	}

	@Test
	public void testDownload() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
		rester.setClient(client);
		rester.setBucket("p1001");
		rester.setDomain("p1001.qiniudn.com");
		rester.setMac(QiniuBucketMacFactory.getBucketMac(rester.getBucket()).getMac());
		String soucePath = "iscript/20150210/ConfigProxyChecker/upload/ConfigProxyChecker.upload.1425793736885.gz";
		soucePath = "iscript/20150309/ConfigProxyChecker/5df50e4e-ad2a-4980-b72d-6eaf32231e2e/ConfigProxyChecker.20150309.1425888905647.gz";
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
