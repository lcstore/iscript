package com.lezo.iscript.yeam.client.utils;

import java.io.File;
import java.io.FileFilter;

public class VersionUtils {

	public static File getVersion(File workspace) {
		if (workspace == null) {
			return null;
		}
		File[] vFiles = workspace.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches("^[0-9]+.version$");
			}
		});
		if (vFiles == null) {
			return null;
		}
		File newVersion = null;
		long maxVersion = 0;
		for (File vFile : vFiles) {
			if (newVersion == null) {
				newVersion = vFile;
				String version = newVersion.getName().replace(".version", "");
				maxVersion = Long.valueOf(version);
			} else {
				String version = vFile.getName().replace(".version", "");
				Long curVersion = Long.valueOf(version);
				if (maxVersion < curVersion) {
					newVersion = vFile;
					maxVersion = curVersion;
				}
			}
		}
		return newVersion;
	}
}
