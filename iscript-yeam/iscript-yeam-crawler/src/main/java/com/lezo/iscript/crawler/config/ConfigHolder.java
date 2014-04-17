package com.lezo.iscript.crawler.config;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigHolder {
	protected static final String CONFIG_NAME_SUFFIX = ".js";
	private static final Object NEW_LINE_MARK = "\n";
	private ConcurrentHashMap<String, String> configMap = new ConcurrentHashMap<String, String>();
	private String cofigFolder;

	public void initConfig() {
		File folder = new File(cofigFolder);
		File[] configArr = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return isConfig(pathname);
			}
		});
		if (configArr != null) {
			for (File file : configArr) {
				try {
					configMap.put(getConfigName(file), getConfig(file));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public boolean isConfig(File file) {
		return file.isFile() && file.getName().endsWith(CONFIG_NAME_SUFFIX);
	}

	public String getConfig(File file) throws Exception {
		if (!file.exists()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		FileReader fRead = null;
		try {
			fRead = new FileReader(file);
			br = new BufferedReader(fRead);
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(NEW_LINE_MARK);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			closeQuiet(br);
			closeQuiet(fRead);
		}
		return sb.toString();
	}

	public String getConfigName(File config) {
		String name = config.getName();
		return name.substring(0, name.length() - CONFIG_NAME_SUFFIX.length());
	}

	private void closeQuiet(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
			}
		}
	}

	public String getConfig(String type) {
		return configMap.get(type);
	}

	public String getCofigFolder() {
		return cofigFolder;
	}

	public void setCofigFolder(String cofigFolder) {
		this.cofigFolder = cofigFolder;
	}
}
