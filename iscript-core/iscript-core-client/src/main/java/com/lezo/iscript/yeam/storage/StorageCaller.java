package com.lezo.iscript.yeam.storage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.utils.PropertiesUtils;

public class StorageCaller {
	private static final int CORE_SIZE = 1;
	private static final int MAX_SIZE = CORE_SIZE;
	private static final int STORAGE_CAPACITY = Integer.valueOf(PropertiesUtils.getProperty("storage_capacity"));
	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 60 * 1000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(STORAGE_CAPACITY));
	private static final StorageCaller INSTANCE = new StorageCaller();

	private StorageCaller() {
	}

	public static StorageCaller getInstance() {
		return INSTANCE;
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

	public void execute(Runnable command) {
		executor.execute(command);
	}

}
