package com.lezo.iscript.yeam.server.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.common.NameThreadFactory;

public class RequestProceser {
	private static RequestProceser instance;
	private int coreSize = 3;
	private int maxSize = 3;
	private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(1000);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, 60000L, TimeUnit.MILLISECONDS,
			taskQueue, new NameThreadFactory("RequestProceser-"));

	private RequestProceser() {
	}

	public static RequestProceser getInstance() {
		if (instance == null) {
			synchronized (RequestProceser.class) {
				if (instance == null) {
					instance = new RequestProceser();
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
