package com.lezo.iscript.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateFormatUtils {
	private static Logger log = Logger.getLogger(DateFormatUtils.class);

	public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";

	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static Date parse(String source, String pattern) {
		if (source == null) {
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.parse(source);
		} catch (ParseException e) {
			log.warn("Fail to parse date:" + source);
		}
		return null;
	}
}
