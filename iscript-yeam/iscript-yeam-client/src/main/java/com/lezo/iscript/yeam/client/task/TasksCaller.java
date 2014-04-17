package com.lezo.iscript.yeam.client.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.yeam.ClientConstant;

public class TasksCaller {
	private static final int corePoolSize = 2;
	private static final int maximumPoolSize = 5;
	private static final long keepAliveTime = 60 * 1000;
	private static final int capacity = ClientConstant.MAX_TASK_QUEUE_CAPACITY;
	private static final BlockingQueue<Runnable> resultQueue = new ArrayBlockingQueue<Runnable>(capacity);
	private final ThreadPoolExecutor caller = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
			TimeUnit.MILLISECONDS, resultQueue);

	private TasksCaller() {
	}

	private final static class TasksCallerHolder {
		private final static TasksCaller INSTANCE = new TasksCaller();
	}

	public static TasksCaller getInstance() {
		return TasksCallerHolder.INSTANCE;
	}

	public ThreadPoolExecutor getCaller() {
		return caller;
	}
}
