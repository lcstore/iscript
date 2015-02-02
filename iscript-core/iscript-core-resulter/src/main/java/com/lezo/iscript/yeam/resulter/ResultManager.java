package com.lezo.iscript.yeam.resulter;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.yeam.resulter.handle.ResultHandleCaller;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultManager implements Resultable {
	private static final int corePoolSize = 2;
	private static final int maximumPoolSize = 5;
	private static final long keepAliveTime = 60 * 1000;
	private static final int capacity = 1000;
	private static final BlockingQueue<Runnable> resultQueue = new ArrayBlockingQueue<Runnable>(capacity);
	private final ThreadPoolExecutor caller = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
			TimeUnit.MILLISECONDS, resultQueue);

	public void doCall(List<ResultWritable> resultList) {
		if (!CollectionUtils.isEmpty(resultList)) {
			caller.execute(new ResultHandleCaller(resultList));
		}
	}
}
