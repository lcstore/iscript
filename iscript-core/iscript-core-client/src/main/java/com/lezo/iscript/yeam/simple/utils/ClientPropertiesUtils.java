package com.lezo.iscript.yeam.simple.utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ClientPropertiesUtils {
	private static Properties properties;
	static {
		try {
			properties = loadPropeties();
		} catch (IOException e) {
			throw new RuntimeException("can not load properties", e);
		}

	}

	public static String getProperty(String key) {
		String value = properties.getProperty(key);
		value = StringUtils.isEmpty(value) ? System.getProperty(key) : value;
		value = StringUtils.isEmpty(value) ? System.getenv(key) : value;
		return value;
	}

	protected static Properties loadPropeties() throws IOException {
		String proName = "config/client.properties";
		InputStream in = null;
		BufferedInputStream inStream = null;
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(proName);
			if (in == null) {
				throw new FileNotFoundException("Can not found [" + proName + "] in work space.");
			}
			Properties pro = new Properties();
			inStream = new BufferedInputStream(in);
			pro.load(inStream);
			return pro;
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(inStream);
		}

	}
}
