package com.lezo.rest.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.rest.QiniuBucketMacFactory;

public class QiniuResterTest {
	private static final String CHARSET_NAME = "UTF-8";
	String accessToken = "21.508cd049e4261c79a35e6f5ff91ae819.2592000.1428294337.4026763474-2920106";
	String rootPath = "/apps/idocs";
	QiniuRester rester = new QiniuRester();

//	@Test
	public void testUpload() throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		rester.setClient(client);
		rester.setBucket("p1001");
		rester.setDomain("p1001.qiniudn.com");
		rester.setMac(QiniuBucketMacFactory.getBucketMac(rester.getBucket()).getMac());

		String targetPath = "iscript/20150210/ConfigProxyChecker/upload/ConfigProxyChecker.upload." + System.currentTimeMillis() + ".gz";
		String source = "rest.test.stt\r\n1233444565475474";
		byte[] dataBytes = source.getBytes();
		Assert.assertTrue(rester.upload(targetPath, dataBytes));
	}

//	@Test
	public void testUploadLines() throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
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
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		rester.setClient(client);
		rester.setBucket("p1001");
		rester.setDomain("p1001.qiniudn.com");
		rester.setMac(QiniuBucketMacFactory.getBucketMac(rester.getBucket()).getMac());
		String soucePath = "iscript/20150210/ConfigProxyChecker/upload/ConfigProxyChecker.upload.1425793736885.gz";
		soucePath = "iscript/20150309/ConfigProxyChecker/5df50e4e-ad2a-4980-b72d-6eaf32231e2e/ConfigProxyChecker.20150309.1425888905647.gz";
		soucePath = "iscript/20150311/ConfigProxyDetector/1e68a446-a6ab-4c91-91c3-7d0f272e862c/ConfigProxyDetector.20150311.1426043050683.gz";
		String content = rester.download(soucePath);
		System.err.println("data:" + content);
		StringTokenizer tokenizer = new StringTokenizer(content);
		List<String> lineList = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
//			lineList.add(tokenizer.nextElement().toString());
			System.out.println(tokenizer.nextElement().toString());
		}
	}

	@Test
	public void testLists() throws Exception {
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		rester.setClient(client);
		rester.setBucket("p1001");
		rester.setDomain("p1001.qiniudn.com");
		rester.setMac(QiniuBucketMacFactory.getBucketMac(rester.getBucket()).getMac());

		String targetPath = "iscript/20150210/ConfigProxyChecker/upload";
		Map<String, String> paramMap = new HashMap<String, String>();
		RestList fileList = rester.listFiles(targetPath, paramMap);
		System.err.println("data:" + fileList.getDataList().size());
	}

//	@Test
	public void testStringTokenizer() throws IOException {
		String content = FileUtils.readFileToString(new File("src/main/resources/file.temp"), "UTF-8");
		StringTokenizer tokenizer = new StringTokenizer(content, "\n");
		List<String> lineList = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
//			lineList.add(tokenizer.nextElement().toString());
			System.out.println(tokenizer.nextElement().toString());
		}
	}
}
