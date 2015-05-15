package com.lezo.iscript.updater.utils;

import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;

public class NameUtils {
	public static String APP_NAME = buildName();

	public static String buildName() {
		String userName = PropertiesUtils.getProperty("client_name");
		userName = StringUtils.isBlank(userName) ? System.getenv("client_name") : userName;
		userName = StringUtils.isBlank(userName) ? System.getProperty("user.name", "unknown") : userName;
		String localHost = "UNKNOWN";
		try {
			localHost = InetUtils.getWANHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return String.format("%s@%s", userName, localHost);
	}
}
