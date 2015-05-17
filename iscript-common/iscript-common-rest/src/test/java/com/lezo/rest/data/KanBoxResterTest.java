package com.lezo.rest.data;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.rest.http.HttpClientUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月12日
 */
public class KanBoxResterTest {
	static KanBoxRester rester = new KanBoxRester();
	static {
		rester.setBucket("pis_1001");
		rester.setDomain("kanbox.com");
		rester.setAccessToken("253966875101559bf51eac3a5a41ba62");
		try {
			DefaultHttpClient client = HttpClientUtils.createHttpClient();
			rester.setClient(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpload() throws Exception {
		String targetPath = "/iscript/20150314/1001.gz";
		String source = "rest.我们.sttrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrestrest";
		byte[] dataBytes = source.getBytes();
		int oldLen = dataBytes.length;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(bos);
		gos.write(dataBytes);
		gos.flush();
		gos.close();
		dataBytes = bos.toByteArray();
		System.err.println("oldLen=" + oldLen + ",newLen=" + dataBytes.length);
		Assert.assertTrue(rester.upload(targetPath, dataBytes));
	}

	@Test
	public void testDownload() throws Exception {
		String soucePath = "/tasker.properties";
		soucePath = "/1.txt";
		System.err.println("data:" + rester.download(soucePath));
	}

	@Test
	public void testListFiles() throws Exception {
		String sourcePath = "/";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("hash", "1426318124");
		RestList restList = rester.listFiles(sourcePath, paramMap);
		System.err.println("data:" + restList.getDataList().size());
	}
}
