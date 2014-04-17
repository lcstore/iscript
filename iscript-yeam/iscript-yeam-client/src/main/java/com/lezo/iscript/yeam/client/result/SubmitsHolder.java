package com.lezo.iscript.yeam.client.result;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SubmitsHolder {
	private final BlockingQueue<SubmitFuture> submitQueue = new LinkedBlockingQueue<SubmitFuture>();

	private static class InstanceHolder {
		private static final SubmitsHolder instance = new SubmitsHolder();
	}

	public static SubmitsHolder getInstance() {
		return InstanceHolder.instance;
	}

	public BlockingQueue<SubmitFuture> getSubmitQueue() {
		return submitQueue;
	}

}
