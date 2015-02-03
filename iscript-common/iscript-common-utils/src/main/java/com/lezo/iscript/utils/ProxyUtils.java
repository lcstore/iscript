package com.lezo.iscript.utils;

public class ProxyUtils {

	public static boolean isPort(int port) {
		if (port < 0 || port > 0xFFFF) {
			return false;
		}
		return true;
	}
}
