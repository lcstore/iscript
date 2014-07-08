package com.lezo.iscript.yeam.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class CodeParser {

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
		oReg = Pattern.compile("dp/([0-9a-zA-Z]{8,})/");
		matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		// yhd
		oReg = Pattern.compile("item/([0-9a-zA-Z]{6,})");
		matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
}
