package com.lezo.rest.data;

import java.util.Map;

public interface DataRestable {

	boolean upload(String targetPath, byte[] dataBytes) throws Exception;

	public String download(String soucePath) throws Exception;

	public RestFileList listFiles(String sourcePath, Map<String, String> paramMap) throws Exception;

}
