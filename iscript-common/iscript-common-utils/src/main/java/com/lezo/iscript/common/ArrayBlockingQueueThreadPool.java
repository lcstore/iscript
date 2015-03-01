package com.lezo.iscript.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ArrayBlockingQueueThreadPool extends ThreadPoolExecutor {
	private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

	public ArrayBlockingQueueThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, int capacity) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
				capacity), Executors.defaultThreadFactory(), defaultHandler);
	}

	public ArrayBlockingQueueThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, int capacity,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
				capacity), Executors.defaultThreadFactory(), handler);
	}

	public ArrayBlockingQueueThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, int capacity,
			ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
				capacity), threadFactory, handler);
	}
}
