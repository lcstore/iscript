package com.lezo.iscript.yeam.client.result;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultsHolder {
	private final BlockingQueue<Future<ResultWritable>> resultQueue = new LinkedBlockingQueue<Future<ResultWritable>>();

	private static class InstanceHolder {
		private static final ResultsHolder instance = new ResultsHolder();
	}

	public static ResultsHolder getInstance() {
		return InstanceHolder.instance;
	}

	public BlockingQueue<Future<ResultWritable>> getResultQueue() {
		return resultQueue;
	}

}
