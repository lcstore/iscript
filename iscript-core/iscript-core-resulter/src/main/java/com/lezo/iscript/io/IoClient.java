package com.lezo.iscript.io;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

import com.lezo.iscript.common.NameThreadFactory;

@Getter
public class IoClient {
	private ThreadPoolExecutor executor;
	private AtomicLong fromMills = new AtomicLong();
	private AtomicLong toMills = new AtomicLong();
	private AtomicLong callCount = new AtomicLong();
	private final int capacity;

	public IoClient(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			int capacity, String name) {
		super();
		this.capacity = capacity;
		this.executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(capacity),
				new NameThreadFactory(name));

	}

	public void execute(Runnable command) {
		executor.execute(command);
		callCount.incrementAndGet();
		fromMills.compareAndSet(0, System.currentTimeMillis());
		toMills.set(System.currentTimeMillis());
	}

	public int size() {
		return this.executor.getQueue().size();
	}

}
