package com.lezo.iscript.yeam.defend.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class ClientLocker {
	private static final String LOCK_SUFFIX = ".lock";
	private File parent;

	public ClientLocker(File parent) {
		super();
		this.parent = parent;
	}

	public ClientLocker(String path) {
		this(new File(path));
	}

	public boolean tryLock(String name) {
		if (StringUtils.isEmpty(name)) {
			return true;
		}
		File file = new File(parent, name + LOCK_SUFFIX);
		return !file.exists();
	}

	public void doLock(String name) throws IOException {
		if (!tryLock(name)) {
			return;
		}
		File file = new File(parent, name);
		file.delete();
		File fileLock = new File(parent, name + LOCK_SUFFIX);
		String data = "" + System.currentTimeMillis();
		FileUtils.writeStringToFile(fileLock, data);
	}

	public boolean unLock(String name) {
		if (tryLock(name)) {
			return false;
		}
		File file = new File(parent, name + LOCK_SUFFIX);
		File dest = new File(parent, name);
		dest.delete();
		file.renameTo(dest);
		return dest.exists();
	}
}
