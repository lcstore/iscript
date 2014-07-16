package com.lezo.iscript.yeam.simple.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ResponeProceser {
	private static ResponeProceser instance;
	private int coreSize = 1;
	private int maxSize = 3;
	private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(100);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, 60000L, TimeUnit.MILLISECONDS,
			taskQueue);

	private ResponeProceser() {
	}

	public static ResponeProceser getInstance() {
		if (instance == null) {
			synchronized (ResponeProceser.class) {
				if (instance == null) {
					instance = new ResponeProceser();
				}
			}
		}
		return instance;
	}

	public void execute(Runnable command) {
		executor.execute(command);
	}

	public BlockingQueue<Runnable> getTaskQueue() {
		return taskQueue;
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

}
