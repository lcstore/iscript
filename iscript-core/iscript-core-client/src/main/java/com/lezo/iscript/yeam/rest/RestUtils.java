package com.lezo.iscript.yeam.rest;

import java.io.File;
import java.io.InputStream;

public class RestUtils {

	public static Object doRestCallBack(File destFile, InputStream in, RestCallBack restCallBack) throws Exception {
		return restCallBack.doCallBack(destFile, in);
	}
}
