package com.lezo.iscript.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;

public class BlockPolicy implements RejectedExecutionHandler {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(BlockPolicy.class);

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		if (!executor.isShutdown()) {
			long start = System.currentTimeMillis();
			logger.info("{}.Start to put task:{}", executor, r);
			BlockingQueue<Runnable> wQueue = executor.getQueue();
			try {
				wQueue.put(r);
				long cost = System.currentTimeMillis() - start;
				logger.info("{}.Finish to put task,cost:{}", executor, cost);
			} catch (InterruptedException e) {
				long cost = System.currentTimeMillis() - start;
				logger.warn(executor + ".put task:" + r + ",cost:" + cost + ",cause:", e);
			}
		}
	}
}
