package com.lezo.iscript.yeam.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.yeam.mina.utils.ClientPropertiesUtils;

public class TasksCaller {
	private static final int corePoolSize = Integer.valueOf(ClientPropertiesUtils.getProperty("task_worker_core"));
	private static final int maximumPoolSize = corePoolSize;
	private static final long keepAliveTime = 60 * 1000;
	private static final int capacity = 50;
	private static final BlockingQueue<Runnable> resultQueue = new ArrayBlockingQueue<Runnable>(capacity);
	private final ThreadPoolExecutor caller = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, resultQueue);

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
