package com.lezo.iscript.yeam.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassUtils {
	private static final Pattern SRC_REG = Pattern.compile("SourceFile.*?(([A-Z]{1}[0-9a-zA-Z]+))\\.java");
	private static List<Pattern> namePatterns = new ArrayList<Pattern>();
	static {
		namePatterns.add(Pattern.compile("LocalVariableTable.*?this.*?([a-z]{1}([0-9a-zA-Z]+/)+([A-Z][0-9a-zA-Z$]+))"));
		namePatterns.add(Pattern.compile("SourceFile.*?.java.*?([a-z]{1}([0-9a-zA-Z]+/)+([A-Z][0-9a-zA-Z$]+))"));
	}
	private static final Pattern PACKAGE_REG = Pattern.compile("[\\s]*package[\\s]+([a-zA-Z.]+)[\\s]*;");
	private static final Pattern CLASS_NAME_REG = Pattern
			.compile("[\\s]*public.*?class.*?([a-zA-Z]+[0-9a-zA-Z_$]+)[\\s]+");

	public static String getClassNameFromClass(String clsSource) {
		String sHead = clsSource;
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

	public static String getClassNameFromJava(String source) {
		String packageName = null;
		Matcher matcher = PACKAGE_REG.matcher(source);
		int start = 1;
		if (matcher.find()) {
			packageName = matcher.group(start);
		}
		matcher = CLASS_NAME_REG.matcher(source);
		if (matcher.find() && matcher.find(start)) {
			String name = matcher.group(start);
			return packageName == null ? name : packageName + "." + name;
		}
		return null;
	}
}