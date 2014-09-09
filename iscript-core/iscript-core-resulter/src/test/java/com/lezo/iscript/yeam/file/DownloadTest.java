package com.lezo.iscript.yeam.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.URLUtils;

public class DownloadTest {

	@Test
	public void test() throws Exception {
		Config.ACCESS_KEY = "dwCDiS4sTkm_8aXoesOHIvFKy65OdrBskcxThAmv";
		Config.SECRET_KEY = "vflRTCRrydngKk7QRYcTh7BYmsG-9KeH-NET4riL";
		Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
		String domain = "istore.qiniudn.com";
		String key = "iscript/20140905/ConfigProxyCollector/-/ConfigProxyCollector.20140905.1409849670453.gz";
		String baseUrl = URLUtils.makeBaseUrl(domain, key);
		GetPolicy getPolicy = new GetPolicy();
		String downloadUrl = getPolicy.makeRequest(baseUrl, mac);
		HttpGet fileGet = new HttpGet(downloadUrl);
		DefaultHttpClient client = HttpClientUtils.createHttpClient();
		HttpResponse res = client.execute(fileGet);
		InputStream inStream = res.getEntity().getContent();
		FileOutputStream fs = new FileOutputStream(new File("src/test/resources/ConfigProxyCollector.gz"));
		byte[] buffer = new byte[1204];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			fs.write(buffer, 0, len);
		}
		inStream.close();
		fs.close();
	}
}
