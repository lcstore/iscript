package com.lezo.rest.qiniu;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.lezo.rest.QiniuBucketMac;
import com.lezo.rest.QiniuBucketMacFactory;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.BatchCallRet;
import com.qiniu.api.rs.EntryPath;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;
import com.qiniu.api.rs.URLUtils;
import com.qiniu.api.rsf.ListItem;
import com.qiniu.api.rsf.ListPrefixRet;
import com.qiniu.api.rsf.RSFClient;

public class QiniuCloud {
	static class Config {
		static String ACCESS_KEY = "dwCDiS4sTkm_8aXoesOHIvFKy65OdrBskcxThAmv";
		static String SECRET_KEY = "vflRTCRrydngKk7QRYcTh7BYmsG-9KeH-NET4riL";
	}

	@Test
	public void test() throws Exception {
		Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
		// 请确保该bucket已经存在
		String bucketName = "istore";
		PutPolicy putPolicy = new PutPolicy(bucketName);
		String uptoken = putPolicy.token(mac);
		PutExtra extra = new PutExtra();
		String key = "test/doc/pcs.token." + System.currentTimeMillis();
		String localFile = "src/main/resources/pcs.token";
		PutRet ret = IoApi.putFile(uptoken, key, localFile, extra);
		System.out.println(ret.response);
	}

	@Test
	public void testDelete() throws Exception {
		String bucketName = "p1001";
		QiniuBucketMac bucketMac = QiniuBucketMacFactory.getBucketMac(bucketName);
		Mac mac = bucketMac.getMac();
		RSClient rs = new RSClient(mac);
		RSFClient client = new RSFClient(mac);
		String key = "iscript/2014";
		String marker = "";
		ListPrefixRet ret = null;
		while (true) {
			ret = client.listPrifix(bucketName, key, marker, 100);
			marker = ret.marker;
			List<EntryPath> entries = new ArrayList<EntryPath>();
			for (ListItem item : ret.results) {
				EntryPath e2 = new EntryPath();
				e2.bucket = bucketName;
				e2.key = item.key;
				entries.add(e2);
			}
			if (!entries.isEmpty()) {
				BatchCallRet bsRet = rs.batchDelete(entries);
				System.err.println(bsRet.response + ",count:" + entries.size());
			}
			if (!ret.ok()) {
				break;
			}
		}
	}

	@Test
	public void testDownData() throws Exception {
		List<String> dataList = downData();
		System.err.println(dataList.size());
	}

	@Test
	public void testListData() throws Exception {
		String bucket = "p1002";
		QiniuBucketMac bucketMac = QiniuBucketMacFactory.getBucketMac(bucket);
		RSFClient client = new RSFClient(bucketMac.getMac());
		ListPrefixRet ret = null;
		int limit = 100;
		String maker = "";
		ret = client.listPrifix(bucketMac.getBucket(), "iscript/20150210/ConfigProxyChecker", maker, limit);
		System.err.println(ret.response);
	}

	private List<String> downData() throws EncoderException, AuthException {
		// iscript/20150208/ConfigProxyChecker/3baa61ec-df2a-4212-9f10-c6a1fa09ea39/ConfigProxyChecker.20150208.1423390018375.gz
		String bucket = "p1002";
		String bucketDomain = bucket + "." + "qiniudn.com";
		String baseUrl = URLUtils.makeBaseUrl(bucketDomain, "iscript/20150210/ConfigProxyChecker/2852e52c-5352-4260-bd3d-967aca318640/ConfigProxyChecker.20150210.1423497617611.gz");
		GetPolicy getPolicy = new GetPolicy();
		QiniuBucketMac bucketMac = QiniuBucketMacFactory.getBucketMac(bucket);
		String downloadUrl = getPolicy.makeRequest(baseUrl, bucketMac.getMac());
		HttpGet fileGet = new HttpGet(downloadUrl);
		InputStream inStream = null;
		DefaultHttpClient CLIENT = new DefaultHttpClient();
		try {
			HttpResponse res = CLIENT.execute(fileGet);
			inStream = res.getEntity().getContent();
			return toDataList(inStream);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		} finally {
			IOUtils.closeQuietly(inStream);
			if (fileGet != null && !fileGet.isAborted()) {
				fileGet.abort();
			}
		}
	}

	private List<String> toDataList(InputStream inStream) throws Exception {
		if (inStream == null) {
			return Collections.emptyList();
		}
		GZIPInputStream gis = new GZIPInputStream(inStream);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] tmp = new byte[1024]; // Rough estimate
		int len = -1;
		while ((len = gis.read(tmp)) > -1) {
			bos.write(tmp, 0, len);
		}
		bos.flush();
		byte[] byteArray = bos.toByteArray();
		bos.close();
		return toStringList(byteArray);
	}

	private List<String> toStringList(byte[] byteArray) throws Exception {
		String fileData = new String(byteArray, "UTF-8");
		StringTokenizer tokenizer = new StringTokenizer(fileData, "\n");
		List<String> stringList = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			stringList.add(tokenizer.nextToken());
		}
		return stringList;
	}
}
