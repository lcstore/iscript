package com.lezo.iscript.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class ConnectUtils {
	private static Logger log = Logger.getLogger(ConnectUtils.class);

	public static int getResponeseCode(String url, int timeout) {
		// Otherwise an exception may be thrown on invalid SSL certificates.
		url = url.replaceFirst("https", "http");
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");

			int responseCode = connection.getResponseCode();
			return responseCode;
		} catch (IOException e) {
			log.warn("Get Responese Code fail..");
		}
		return -1;
	}

	public static String getURLBase(String url) {
		try {
			URL u = new URL(url);
			String path = u.getPath();
			String[] pArr = path.split("/");
			String sBase = url;
			if (pArr.length > 2) {
				int index = sBase.indexOf(pArr[2]);
				sBase = (index > 0) ? sBase.substring(0, index + pArr[2].length()) : sBase;
			}
			return sBase;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isAlive(String url, int timeout) {
		String sBase = getURLBase(url);
		int code = getResponeseCode(sBase, timeout);
		return code >= HttpURLConnection.HTTP_OK && code < 300;
	}
}
