package com.lezo.iscript.yeam.storage;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StorageCaller {
	private static final int CORE_SIZE = 2;
	private static final int MAX_SIZE = 3;
	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 60 * 1000L,
			TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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
