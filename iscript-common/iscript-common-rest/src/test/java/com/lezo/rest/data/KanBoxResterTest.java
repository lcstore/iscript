package com.lezo.rest.data;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.rest.baidu.pcs.PcsClient;

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
		rester.setAccessToken("bd8cefd7566aabb7a305d31297ecd46e");
		try {
			DefaultHttpClient client = new DefaultHttpClient(PcsClient.createClientConnManager());
			rester.setClient(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpload() throws Exception {
		String targetPath = "/1001.txt";
		String source = "rest.我们.stt";
		byte[] dataBytes = source.getBytes();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(bos);
		gos.write(dataBytes);
		gos.flush();
		gos.close();
//		dataBytes = bos.toByteArray();
		Assert.assertTrue(rester.upload(targetPath, dataBytes));
	}

	@Test
	public void testDownload() throws Exception {
		String soucePath = "/tasker.properties";
		System.err.println("data:" + rester.download(soucePath));
	}
}
