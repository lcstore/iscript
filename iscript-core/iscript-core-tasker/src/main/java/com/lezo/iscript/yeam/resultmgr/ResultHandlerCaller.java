package com.lezo.iscript.yeam.resultmgr;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lezo.iscript.common.NameThreadFactory;

public class ResultHandlerCaller {
	private static final int CORE_SIZE = 2;
	private static final int MAX_SIZE = 3;
	private ThreadPoolExecutor executor;
	private static final ResultHandlerCaller INSTANCE = new ResultHandlerCaller();

	private ResultHandlerCaller() {
		this.executor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 60 * 1000L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), new NameThreadFactory("ResultHandler-"));
	}

	public static ResultHandlerCaller getInstance() {
		return INSTANCE;
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

	public void execute(Runnable command) {
		executor.execute(command);
	}

}
