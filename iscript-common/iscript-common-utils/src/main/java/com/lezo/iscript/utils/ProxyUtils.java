package com.lezo.iscript.utils;

public class ProxyUtils {

	public static boolean isPort(Integer port) {
		if (port == null || port < 0 || port > 0xFFFF) {
			return false;
		}
		return true;
	}
}
