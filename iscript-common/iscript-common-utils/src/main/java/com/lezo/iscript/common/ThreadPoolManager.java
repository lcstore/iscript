package com.lezo.iscript.common;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolManager {
	private static final ConcurrentHashMap<String, ThreadPoolExecutor> EXECUTOR_MAP = new ConcurrentHashMap<String, ThreadPoolExecutor>();

	public ThreadPoolManager() {
	}

	public ThreadPoolManager(Map<String, ThreadPoolExecutor> executorMap) {
		for (Entry<String, ThreadPoolExecutor> entry : executorMap.entrySet()) {
			addExecutor(entry.getKey(), entry.getValue());
		}
	}

	public static boolean addExecutor(String name, ThreadPoolExecutor executor) {
		if (name == null || executor == null) {
			return false;
		}
		ThreadPoolExecutor oldExecutor = EXECUTOR_MAP.putIfAbsent(name, executor);
		return executor == oldExecutor;
	}

	public static ThreadPoolExecutor getExecutor(String name) {
		return EXECUTOR_MAP.get(name);
	}
}
