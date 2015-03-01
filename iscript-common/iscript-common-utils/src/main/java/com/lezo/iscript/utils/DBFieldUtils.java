package com.lezo.iscript.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBFieldUtils {
	private static final String SPLIT_MARK = " ";
	private static final String PARAM_SPLIT_MARK = "_";
	private static final Pattern PARAM_REG = Pattern.compile("[0-9a-zA-Z_]+");

	public static List<String> sql2Field(List<String> sqlLines) {
		List<String> columnList = new ArrayList<String>();
		for (String sqlLine : sqlLines) {
			sqlLine = sqlLine.trim();
			int endIndex = sqlLine.indexOf(SPLIT_MARK);
			String column = sqlLine.substring(0, endIndex);
			Matcher matcher = PARAM_REG.matcher(column);
			if (matcher.find()) {
				column = column.substring(matcher.start(), matcher.end());
			}
			columnList.add(column);
		}
		return columnList;
	}

	public static List<String> field2Param(List<String> fieldList) {
		List<String> paramList = new ArrayList<String>();
		for (String sqlLine : fieldList) {
			String[] paramArr = sqlLine.split(PARAM_SPLIT_MARK);
			String param = "";
			for (int i = 0; i < paramArr.length; i++) {
				if (i > 0) {
					String next = paramArr[i].toLowerCase();
					next = next.substring(0, 1).toUpperCase() + next.substring(1, next.length());
					param += next;
				} else {
					param = paramArr[i].toLowerCase();
				}
			}
			paramList.add(param);
		}
		return paramList;
	}
}
