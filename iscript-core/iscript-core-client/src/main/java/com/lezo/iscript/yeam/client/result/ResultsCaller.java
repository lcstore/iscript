package com.lezo.iscript.yeam.client.result;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.yeam.ClientConstant;

public class ResultsCaller {
	private static final int corePoolSize = 1;
	private static final int maximumPoolSize = 3;
	private static final long keepAliveTime = 60 * 1000;
	private static final int capacity = ClientConstant.MAX_RESULT_QUEUE_CAPACITY;
	private static final BlockingQueue<Runnable> resultQueue = new ArrayBlockingQueue<Runnable>(capacity);
	private final ThreadPoolExecutor caller = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
			TimeUnit.MILLISECONDS, resultQueue);

	private ResultsCaller() {
	}

	private final static class ResultsCallerHolder {
		private final static ResultsCaller INSTANCE = new ResultsCaller();
	}

	public static ResultsCaller getInstance() {
		return ResultsCallerHolder.INSTANCE;
	}

	public ThreadPoolExecutor getCaller() {
		return caller;
	}
}
