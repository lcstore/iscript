package com.lezo.iscript.updater.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtils {
	private static DefaultHttpClient client = HttpClientFactory.createHttpClient();

	public static DefaultHttpClient getDefaultClient() {
		return client;
	}

	public static String getCharsetOrDefault(Header contentType, byte[] dataBytes, String defaultCharset)
			throws Exception {
		String charset = getCharsetFromHead(contentType);
		if (charset != null) {
			return charset;
		}
		charset = getCharsetFromData(dataBytes);
		if (charset != null) {
			return charset;
		}
		return defaultCharset;
	}

	public static String getCharsetFromHead(Header contentType) throws Exception {
		Header header = contentType;
		if (header != null) {
			HeaderElement[] elements = header.getElements();
			if (elements.length > 0) {
				HeaderElement helem = elements[0];
				NameValuePair param = helem.getParameterByName("charset");
				if (param != null) {
					return param.getValue();
				}
			}
		}
		return null;
	}

	public static String getCharsetFromData(byte[] dataBytes) throws Exception {
		if (dataBytes != null) {
			String souce = new String(dataBytes, "GBK");
			int index = souce.indexOf("Content-Type");
			if (index > 0) {
				int maxLen = 100;
				maxLen = maxLen < souce.length() ? maxLen : souce.length();
				souce = souce.substring(index, index + maxLen);
				Pattern oReg = Pattern.compile("charset.*?=([a-zA-Z0-9\\-]{3,})");
				Matcher matcher = oReg.matcher(souce);
				if (matcher.find()) {
					return matcher.group(1).trim();
				}
			}
		}
		return null;
	}
}
