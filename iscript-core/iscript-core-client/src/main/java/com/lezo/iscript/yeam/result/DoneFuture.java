package com.lezo.iscript.yeam.result;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DoneFuture<V> implements Future<V> {
	private V data;

	public DoneFuture(V data) {
		super();
		this.data = data;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return this.data;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.data;
	}

}
