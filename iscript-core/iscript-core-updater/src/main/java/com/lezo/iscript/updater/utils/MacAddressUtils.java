package com.lezo.iscript.updater.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class MacAddressUtils {

	private static final String[] windowsCommand = { "ipconfig", "/all" };
	private static final String[] linuxCommand = { "/sbin/ifconfig", "-a" };
	private static final Pattern macPattern = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*",
			Pattern.CASE_INSENSITIVE);

	private static List<String> getMacAddressList() throws Exception {
		ArrayList<String> macAddressList = new ArrayList<String>();
		final String os = System.getProperty("os.name").toUpperCase();
		final String[] command;
		if (os.startsWith("WINDOWS")) {
			command = windowsCommand;
		} else if (os.startsWith("LINUX")) {
			command = linuxCommand;
		} else if (os.startsWith("MAC")) {
			String macAddr = getMACAddress(InetAddress.getLocalHost());
			macAddressList.add(macAddr);
			return macAddressList;
		} else {
			throw new IOException("Unknown operating system: " + os);
		}
		final Process process = Runtime.getRuntime().exec(command);
		// Discard the stderr
		new Thread() {

			@Override
			public void run() {
				try {
					InputStream errorStream = process.getErrorStream();
					StringBuffer sb = new StringBuffer();
					int data = -1;
					while ((data = errorStream.read()) != -1) {
						sb.append(data);
					}
					Logger.getRootLogger().debug("errorStream:" + sb.toString());
					errorStream.close();
				} catch (IOException e) {
					Logger.getRootLogger().error(e.getMessage(), e);
				}
			}
		}.start();
		// Extract the MAC addresses from stdout
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		for (String line = null; (line = reader.readLine()) != null;) {
			Matcher matcher = macPattern.matcher(line);
			if (matcher.matches()) {
				// macAddressList.add(matcher.group(1));
				macAddressList.add(matcher.group(1));
			}
		}
		reader.close();
		return macAddressList;
	}

	public static String getMacAddress() {
		try {
			List<String> addressList = getMacAddressList();
			if (addressList.isEmpty()) {
				return "UNKNOWN";
			}
			String addr = addressList.get(0);
			addr = addr.replaceAll("[-:]", "");
			return addr == null ? null : addr.replaceAll("[-:]", "").toUpperCase();
		} catch (Exception e) {
			Logger.getRootLogger().error(e.getMessage(), e);
			return "UNKNOWN";
		}
	}

	public static String[] getMacAddresses() {
		try {
			List<String> addList = getMacAddressList();
			return addList.toArray(new String[addList.size()]);
		} catch (Exception e) {
			Logger.getRootLogger().error(e.getMessage(), e);
			return new String[0];
		}
	}

	public static void main(String[] arguments) throws Exception {
		InetAddress ia = InetAddress.getLocalHost();// 获取本地IP对象
		System.out.println("MAC ......... " + getMACAddress(ia));
	}

	// 获取mac osx的MAC地址
	private static String getMACAddress(InetAddress ia) throws Exception {
		// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

		// 下面代码是把mac地址拼装成String
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// mac[i] & 0xFF 是为了把byte转化为正整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}
		// 把字符串所有小写字母改为大写成为正规的mac地址并返回
		return sb.toString().toUpperCase();
	}
}
