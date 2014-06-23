package com.lezo.iscript.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressUtils {

	/**
	 * the same function to INET_ATON in mysql
	 * 
	 * @param ipString
	 * @return
	 */
	public static int ip2int(String ipString) {
		String[] segments = ipString.split("\\.");
		int i = 0;
		int ipNumber = Integer.parseInt(segments[i++]) << 24;
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
	public static String int2ip(int raw) {
		byte[] ipBtyes = new byte[] { (byte) (raw >> 24), (byte) (raw >> 16), (byte) (raw >> 8), (byte) raw };
		try {
			return InetAddress.getByAddress(ipBtyes).getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
}
