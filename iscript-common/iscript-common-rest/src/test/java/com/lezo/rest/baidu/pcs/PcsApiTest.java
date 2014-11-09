package com.lezo.rest.baidu.pcs;

import java.io.File;

import org.junit.Test;

import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSListInfoResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSSimplefiedResponse;
import com.baidu.pcs.BaiduPCSClient;

public class PcsApiTest {
	String accessToken = "23.4bc6dbd93afb14f64cdfa89af523126c.2592000.1418106914.4026763474-2920106";
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
}
