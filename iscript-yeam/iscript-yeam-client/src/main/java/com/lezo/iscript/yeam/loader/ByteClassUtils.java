package com.lezo.iscript.yeam.loader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteClassUtils {
	private static final Pattern SRC_REG = Pattern.compile("SourceFile.*?(([A-Z]{1}[0-9a-zA-Z]+))\\.java");
	private static List<Pattern> namePatterns = new ArrayList<Pattern>();
	static {
		namePatterns.add(Pattern.compile("LocalVariableTable.*?this.*?([a-z]{1}([0-9a-zA-Z]+/)+([A-Z][0-9a-zA-Z$]+))"));
		namePatterns.add(Pattern.compile("SourceFile.*?.java.*?([a-z]{1}([0-9a-zA-Z]+/)+([A-Z][0-9a-zA-Z$]+))"));
	}

	// get class name from bytes
	public static String getClassName(byte[] bytes) {
		String sHead = "";
		try {
			sHead = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		Matcher matcher = SRC_REG.matcher(sHead);
		if (matcher.find(1)) {
			String javaName = matcher.group(1);
			// think about inner class.ResultsHolder$InstanceHolder
			for (Pattern p : namePatterns) {
				matcher = p.matcher(sHead);
				if (matcher.find(1)) {
					String clsName = matcher.group(1).replace("/", ".");
					if (clsName.indexOf(javaName) > -1) {
						return clsName;
					}
				}
			}
		}
		return null;
	}
}