package com.lezo.iscript.yeam.server.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.common.NameThreadFactory;

public class ResponeProceser {
	private static ResponeProceser instance;
	private int coreSize = 2;
	private int maxSize = 2;
	private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(1000);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, 60000L, TimeUnit.MILLISECONDS,
			taskQueue, new NameThreadFactory("RequestProceser-"));

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
