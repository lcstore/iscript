package com.lezo.iscript.yeam.file;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PersistentCaller {
	private static final int CORE_SIZE = 1;
	private static final int MAX_SIZE = CORE_SIZE;
	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 60 * 1000L,
			TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	private static final PersistentCaller INSTANCE = new PersistentCaller();

	private PersistentCaller() {
	}

	public static PersistentCaller getInstance() {
		return INSTANCE;
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

	public void execute(Runnable command) {
		executor.execute(command);
	}

}
