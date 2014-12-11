package com.lezo.iscript.common;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.log4j.Logger;

public class MacAddress {

	private static final String[] windowsCommand = { "ipconfig", "/all" };
	private static final String[] linuxCommand = { "/sbin/ifconfig", "-a" };
	private static final Pattern macPattern = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*", Pattern.CASE_INSENSITIVE);

	private static List<String> getMacAddressList() throws IOException {
		ArrayList<String> macAddressList = new ArrayList<String>();
		final String os = System.getProperty("os.name");
		final String[] command;
		if (os.startsWith("Windows")) {
			command = windowsCommand;
		} else if (os.startsWith("Linux")) {
			command = linuxCommand;
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
				macAddressList.add(matcher.group(1).replaceAll("[-:]", ""));
			}
		}
		reader.close();
		return macAddressList;
	}

	public static String getMacAddress() {
		try {
			List<String> addressList = getMacAddressList();
			if (addressList.isEmpty()) {
				return "";
			}
			String addr = addressList.get(0);
			return addr == null ? null : addr.toUpperCase();
		} catch (IOException e) {
			Logger.getRootLogger().error(e.getMessage(), e);
			return "";
		}
	}

	public static String[] getMacAddresses() {
		try {
			List<String> addList = getMacAddressList();
			return addList.toArray(new String[addList.size()]);
		} catch (IOException e) {
			Logger.getRootLogger().error(e.getMessage(), e);
			return new String[0];
		}
	}
}
