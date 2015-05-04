package com.lezo.iscript.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class URLUtils {

	public static String getCodeFromUrl(String productUrl) {
		if (StringUtils.isEmpty(productUrl)) {
			return null;
		}
		// jd,yixun,suning,tiantian,jumei,dangdang
		Pattern oReg = Pattern.compile(".*?[/-]{1}([0-9]+).html");
		Matcher matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		// gome,newegg,winxuan,womai
		oReg = Pattern.compile("product[/-]{1}([0-9a-zA-Z-]{6,})", Pattern.CASE_INSENSITIVE);
		matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		// amazon,
		oReg = Pattern.compile("(?<=dp/)[0-9a-zA-Z]{8,}|(?<=asin=)[0-9a-zA-Z]{8,}");
		matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			String code = matcher.group();
			return code != null ? code.toUpperCase() : null;
		}
		// yhd
		oReg = Pattern.compile("item/([0-9a-zA-Z]{6,})");
		matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getRootHost(String url) {
		// http://www.gome.com.cn/product/A0004331780
		String domainString = getHost(url);
		String sMark = "www.";
		String rootDomain = null;
		if (domainString.startsWith(sMark)) {
			rootDomain = domainString.substring(sMark.length());
		} else {
			String[] strArray = domainString.split("\\.");
			StringBuilder sb = new StringBuilder();
			if (strArray.length == 3) {
				sb.append(strArray[1]);
				sb.append(".");
				sb.append(strArray[2]);
				rootDomain = sb.toString();
			} else if (strArray.length > 3) {
				// m.xxx.com.tw,watch out the top
				// domain(.com.tw,.cn.com,.org.cn)
				for (int i = strArray.length - 1; i >= strArray.length - 3; i--) {
					if (sb.length() > 0) {
						sb.insert(0, ".");
					}
					String str = strArray[i];
					sb.insert(0, str);
					if (str.length() > 3) {
						break;
					}
				}
				rootDomain = sb.toString();
			} else if (strArray.length == 2) {
				rootDomain = domainString;
			}
		}
		return rootDomain;
	}

	public static String getHost(String url) {
		String sMark = "://";
		int fromIndex = url.indexOf(sMark);
		fromIndex += sMark.length();
		int toIndex = url.indexOf("/", fromIndex);
		toIndex = toIndex < 0 ? url.length() : toIndex;
		return url.substring(fromIndex, toIndex);
	}
}
