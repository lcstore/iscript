package com.lezo.iscript.yeam.server.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.common.NameThreadFactory;

public class DataProceser {
	private static DataProceser instance;
	private int coreSize = 2;
	private int maxSize = 2;
	private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(1000);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, 60000L, TimeUnit.MILLISECONDS,
			taskQueue, new NameThreadFactory("RequestProceser-"));

	private DataProceser() {
	}

	public static DataProceser getInstance() {
		if (instance == null) {
			synchronized (DataProceser.class) {
				if (instance == null) {
					instance = new DataProceser();
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
