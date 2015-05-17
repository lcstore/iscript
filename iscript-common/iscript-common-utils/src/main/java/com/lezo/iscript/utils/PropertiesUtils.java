package com.lezo.iscript.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;


public class PropertiesUtils {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(PropertiesUtils.class);
	private static final Properties GLOBAL_PROPERTIES = new Properties();

	public static String getOrDefault(String key,String defaultValue) {
		String value = getProperty(key);
		value = StringUtils.isEmpty(value) ? defaultValue : value;
		return value;
	}
	public static String getProperty(String key) {
		String value = GLOBAL_PROPERTIES.getProperty(key);
		value = StringUtils.isEmpty(value) ? System.getProperty(key) : value;
		value = StringUtils.isEmpty(value) ? System.getenv(key) : value;
		return value;
	}

	public synchronized static void loadQuietly(InputStream in) {
		BufferedInputStream inStream = null;
		try {
			inStream = new BufferedInputStream(in);
			GLOBAL_PROPERTIES.load(inStream);
		} catch (IOException e) {
			logger.warn("loadQuietly,cause:", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(inStream);
		}

	}
}
