package com.lezo.iscript.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class HardUtils {
	private static final String[] WINDOWS_UUID_CMD = { "wmic", "csproduct", "get", "uuid" };
	private static final String[] LINUX_UUID_CMD = { "ls", "-l", "/dev/disk/by-uuid" };
	// private static final String[] LINUX_UUID_CMD = { "blkid", "-s", "UUID" };
	private static final Pattern uuidReg = Pattern.compile("([0-9a-zA-z]+-){4}[0-9a-zA-z]+");

	public static List<String> getOSUUIDs() throws IOException {
		final String os = System.getProperty("os.name");
		final String[] command;
		if (os.startsWith("Windows")) {
			command = WINDOWS_UUID_CMD;
		} else if (os.startsWith("Linux")) {
			command = LINUX_UUID_CMD;
		} else {
			throw new IOException("Unknown operating system: " + os);
		}
		final Process process = Runtime.getRuntime().exec(command);
		// Discard the stderr
		new Thread() {
			@Override
			public void run() {
				InputStream errorStream = process.getErrorStream();
				try {
					StringBuffer sb = new StringBuffer();
					int data = -1;
					while ((data = errorStream.read()) != -1) {
						sb.append(data);
					}
					Logger.getRootLogger().debug("errorStream:" + sb.toString());
				} catch (IOException e) {
					Logger.getRootLogger().error(e.getMessage(), e);
				} finally {
					IOUtils.closeQuietly(errorStream);
				}
			}
		}.start();

		List<String> uuidList = new ArrayList<String>();
		// Extract the MAC addresses from stdout
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			for (String line = null; (line = reader.readLine()) != null;) {
				Matcher matcher = uuidReg.matcher(line);
				if (matcher.find()) {
					uuidList.add(matcher.group().toUpperCase());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return uuidList;
	}

	public static String getOSUUID() {
		try {
			List<String> uuidList = getOSUUIDs();
			if (CollectionUtils.isNotEmpty(uuidList)) {
				return uuidList.get(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
