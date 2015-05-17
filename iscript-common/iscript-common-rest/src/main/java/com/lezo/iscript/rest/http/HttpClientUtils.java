package com.lezo.iscript.rest.http;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.lezo.iscript.utils.ProxyUtils;

public class HttpClientUtils {
	public static final String DEFAULT_CHARSET = "UTF-8";

	public static String getContent(DefaultHttpClient client, HttpUriRequest get) throws Exception {
		return getContent(client, get, DEFAULT_CHARSET);
	}

	public static String getContent(DefaultHttpClient client, HttpUriRequest request, String charsetName) throws Exception {
		HttpResponse response = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity, charsetName);
			}
			return null;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (request != null && !request.isAborted()) {
				request.abort();
			}
		}

	}

	public static DefaultHttpClient createHttpClient() {
		return HttpClientFactory.createHttpClient();
	}


	public static String getCharsetOrDefault(Header contentType, byte[] dataBytes, String defaultCharset) throws Exception {
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
