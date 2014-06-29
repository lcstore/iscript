package com.lezo.iscript.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressUtils {

	/**
	 * the same function to INET_ATON in mysql
	 * 
	 * @param ipString
	 * @return
	 */
	@Deprecated
	public static long ip2int(String ipString) {
		String[] segments = ipString.split("\\.");
		int i = 0;
		long ipNumber = Integer.parseInt(segments[i++]) << 24;
		ipNumber += Integer.parseInt(segments[i++]) << 16;
		ipNumber += Integer.parseInt(segments[i++]) << 8;
		ipNumber += Integer.parseInt(segments[i++]);
		return ipNumber;
	}

	/**
	 * the same function to INET_NTOA in mysql
	 * 
	 * @param raw
	 * @return
	 */
	@Deprecated
	public static String int2ip(int raw) {
		byte[] ipBtyes = new byte[] { (byte) (raw >> 24), (byte) (raw >> 16), (byte) (raw >> 8), (byte) raw };
		try {
			return InetAddress.getByAddress(ipBtyes).getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String inet_ntoa(long ipNum) {
		return ((ipNum & 0xff000000) >> 24) + "." + ((ipNum & 0xff0000) >> 16) + "." + ((ipNum & 0xff00) >> 8) + "."
				+ ((ipNum & 0xff));
	}

	public static long inet_aton(Inet4Address add) {
		byte[] bytes = add.getAddress();
		long result = 0;
		for (byte b : bytes) {
			if ((b & 0x80L) != 0) {
				result += 256L + b;
			} else {
				result += b;
			}
			result <<= 8;
		}
		result >>= 8;
		return result;
	}

	/**
	 * significantly faster than parse the string into long
	 */
	public static long inet_aton(String ipString) {
		long result = 0;
		// number between a dot
		long section = 0;
		// which digit in a number
		int times = 1;
		// which section
		int dots = 0;
		for (int i = ipString.length() - 1; i >= 0; --i) {
			if (ipString.charAt(i) == '.') {
				times = 1;
				section <<= dots * 8;
				result += section;
				section = 0;
				++dots;
			} else {
				section += (ipString.charAt(i) - '0') * times;
				times *= 10;
			}
		}
		section <<= dots * 8;
		result += section;
		return result;
	}
}
