package com.lezo.iscript.yeam.resultmgr.directory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

public class DirectoryLockUtils {
	private static ConcurrentHashMap<String, Lock> directoryLockMap = new ConcurrentHashMap<String, Lock>();

	public static Lock findLock(String key) {
		return directoryLockMap.get(key);
	}

	public static void addLock(String key, Lock newLock) {
		directoryLockMap.put(key, newLock);
	}
}
