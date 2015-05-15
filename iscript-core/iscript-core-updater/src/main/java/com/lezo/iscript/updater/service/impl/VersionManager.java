package com.lezo.iscript.updater.service.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.updater.service.IVersionManager;

public class VersionManager implements IVersionManager {
	private static Logger logger = LoggerFactory.getLogger(VersionManager.class);
	private static final String VERSION_NAME = "version.txt";
	private static final String DEFAUL_CHARSET = "UTF-8";
	private String version = "";

	public VersionManager() {
		File versionFile = new File("agent", VERSION_NAME);
		if (versionFile.exists()) {
			try {
				String curVersion = FileUtils.readFileToString(versionFile, DEFAUL_CHARSET);
				curVersion = curVersion == null ? "" : curVersion.trim();
				this.version = curVersion;
			} catch (IOException e) {
				logger.warn("fail to read version file:" + versionFile + ",cause:", e);
			}
		}
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public void changeTo(String newVersion) {
		File versionFile = new File("agent", VERSION_NAME);
		try {
			FileUtils.writeStringToFile(versionFile, newVersion, DEFAUL_CHARSET);
			this.version = newVersion;
		} catch (IOException e) {
			logger.warn("write version.from:" + version + ",to:" + newVersion + ",cause:", e);
		}
	}

}