package com.lezo.iscript.yeam.rest;

import java.io.File;
import java.io.InputStream;

import com.lezo.iscript.yeam.mina.utils.ClientPropertiesUtils;

public class RestUtils {
	public static final RestCallBack QINIU_CALL_BACK = new QiniuRestCallBack(
			ClientPropertiesUtils.getProperty("qiniu_accessKey"), ClientPropertiesUtils.getProperty("qiniu_secretKey"),
			ClientPropertiesUtils.getProperty("qiniu_bucket"));

	public static Object doRestCallBack(File destFile, InputStream in, RestCallBack restCallBack) throws Exception {
			return restCallBack.doCallBack(destFile, in);
	}
}
