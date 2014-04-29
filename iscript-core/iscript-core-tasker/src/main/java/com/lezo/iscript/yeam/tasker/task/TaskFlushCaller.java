package com.lezo.iscript.yeam.tasker.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskFlushCaller {
	private static final int corePoolSize = 2;
	private static final int maximumPoolSize = 5;
	private static final long keepAliveTime = 60 * 1000;
	private static final int capacity = 1000;
	private static final BlockingQueue<Runnable> resultQueue = new ArrayBlockingQueue<Runnable>(capacity);
	private final ThreadPoolExecutor caller = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
			TimeUnit.MILLISECONDS, resultQueue);
	private static TaskFlushCaller instance;

	public static TaskFlushCaller getInstance() {
		if (instance == null) {
			synchronized (TaskFlushCaller.class) {
				if (instance == null) {
					instance = new TaskFlushCaller();
				}
			}
		}
		return instance;
	}

	public ThreadPoolExecutor getCaller() {
		return caller;
	}
}
