package com.lezo.iscript.utils;

import org.apache.commons.lang3.StringUtils;

public class CharsUtils {

	public static String unifyChars(String source) {
		return source == null ? null : source.toLowerCase();
	}

	public static boolean contains(String source, String token) {
		return StringUtils.isNotBlank(token) && StringUtils.isNotBlank(source)
				&& CharsUtils.unifyChars(source).contains(CharsUtils.unifyChars(token));
	}
}
