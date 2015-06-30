package com.lezo.iscript.utils;

import java.io.UnsupportedEncodingException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;

@Log4j
public class CharsUtils {

	public static String unifyChars(String source) {
		return source == null ? null : source.toLowerCase();
	}

	public static boolean contains(String source, String token) {
		return StringUtils.isNotBlank(token) && StringUtils.isNotBlank(source)
				&& CharsUtils.unifyChars(source).contains(CharsUtils.unifyChars(token));
	}

	public static int getCharLength(String o1) {
		return getCharLength(o1, "GBK");
	}

	public static int getCharLength(String o1, String charsetName) {
		if (o1 == null) {
			return -1;
		}
		try {
			return o1.getBytes(charsetName).length;
		} catch (UnsupportedEncodingException e) {
			log.warn("Chars:" + o1 + ",cause:", e);
		}
		return -1;
	}
}
