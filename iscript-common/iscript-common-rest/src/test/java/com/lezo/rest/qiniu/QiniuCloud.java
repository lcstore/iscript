package com.lezo.rest.qiniu;

import org.junit.Test;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;

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
}
