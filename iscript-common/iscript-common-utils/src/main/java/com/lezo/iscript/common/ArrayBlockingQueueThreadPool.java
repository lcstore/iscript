package com.lezo.iscript.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ArrayBlockingQueueThreadPool extends ThreadPoolExecutor {

	public ArrayBlockingQueueThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, int capacity,
			ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
				capacity), threadFactory, new BlockPolicy());
	}

	public ArrayBlockingQueueThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, int capacity,
			ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
				capacity), threadFactory, handler);
	}

}
