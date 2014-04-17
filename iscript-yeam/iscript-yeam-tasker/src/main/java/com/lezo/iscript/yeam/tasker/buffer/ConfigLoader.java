package com.lezo.iscript.yeam.tasker.buffer;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ConfigLoader {
	private static Logger log = Logger.getLogger(ConfigLoader.class);
	private volatile boolean running = false;
	private String configPath;

	public void run() {
		if (running) {
			log.warn("Config loader is working...");
			return;
		}
		try {
			long start = System.currentTimeMillis();
			File configFile = new File(configPath);
			FileFilter filter = getConfigFilter();
			File[] configFiles = configFile.listFiles(filter);
			int index = 0;
			if (configFiles != null) {
				ConfigBuffer configBuffer = ConfigBuffer.getInstance();
				for (File config : configFiles) {
					try {
						String type = TypeUtils.getName(config.getName());
						ConfigWritable configWritable = new ConfigWritable();
						configWritable.setName(type);
						byte[] content = FileUtils.readFileToByteArray(config);
						configWritable.setContent(content);
						configWritable.setType(getConfigType(config));
						configWritable.setStamp(System.currentTimeMillis());
						configBuffer.addConfig(configWritable.getName(), configWritable);
						index++;
					} catch (Exception e) {
						log.warn("Can not load config[" + config + "]", e);
					}
				}
			}
			long cost = System.currentTimeMillis() - start;
			log.info("load config:" + index + ",cost:" + cost + "ms");
		} finally {
			running = false;
		}

	}

	private int getConfigType(File config) {
		String name = config.getName();
		return name.toLowerCase().lastIndexOf("class") > 0 ? ConfigWritable.CONFIG_TYPE_JAVA
				: ConfigWritable.CONFIG_TYPE_SCRIPT;
	}

	private FileFilter getConfigFilter() {
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && isAcceptType(pathname) && isNewConfig(pathname);
			}
		};
		return filter;
	}

	protected boolean isNewConfig(File pathname) {
		long stamp = ConfigBuffer.getInstance().getStamp();
		return pathname.lastModified() > stamp;
	}

	public boolean isAcceptType(File pathname) {
		String name = pathname.getName().toLowerCase();
		return name.endsWith(".xml") || name.endsWith(".class");
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

}
