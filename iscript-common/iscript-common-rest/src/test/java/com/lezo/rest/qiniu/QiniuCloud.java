package com.lezo.rest.qiniu;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.BatchCallRet;
import com.qiniu.api.rs.BatchStatRet;
import com.qiniu.api.rs.EntryPath;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;
import com.qiniu.api.rsf.ListItem;
import com.qiniu.api.rsf.ListPrefixRet;
import com.qiniu.api.rsf.RSFClient;
import com.qiniu.api.rsf.RSFEofException;

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
		Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
		// 请确保该bucket已经存在
		String bucketName = "istore";
		RSClient rs = new RSClient(mac);
		RSFClient client = new RSFClient(mac);
		String key = "iscript/201409";
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
}
