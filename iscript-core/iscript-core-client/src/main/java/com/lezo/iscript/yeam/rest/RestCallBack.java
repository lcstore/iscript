package com.lezo.iscript.yeam.rest;

import java.io.File;
import java.io.InputStream;

public interface RestCallBack {
	public Object doCallBack(File destFile, InputStream in) throws Exception;
}
