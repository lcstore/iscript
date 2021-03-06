package com.lezo.iscript.yeam.rest;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.lezo.rest.QiniuBucketMac;
import com.lezo.rest.QiniuBucketMacFactory;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;

public class QiniuRestCallBack implements RestCallBack {

	private Mac mac;
	private PutPolicy putPolicy;

	public QiniuRestCallBack(String bucket) {
		QiniuBucketMac bucketMac = QiniuBucketMacFactory.getBucketMac(bucket);
		this.mac = bucketMac.getMac();
		this.putPolicy = new PutPolicy(bucketMac.getBucket());
	}

	@Override
	public Object doCallBack(File destFile, InputStream in) throws Exception {
		String key = toQiniuKey(destFile.toString());
		PutExtra extra = new PutExtra();
		String uptoken = putPolicy.token(mac);
		PutRet ret = IoApi.Put(uptoken, key, in, extra);
		if (ret.exception != null) {
			throw ret.exception;
		}
		return ret;
	}

	private String toQiniuKey(String key) {
		String newKey = key.replace("\\", "/");
		try {
			newKey = new String(newKey.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newKey;
	}

}
