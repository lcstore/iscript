package com.lezo.iscript.common.queue;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ArrayBlockingQueueFactory<T> implements QueueFactory<T> {

	@Override
	public Queue<T> newQueue(int capacity) {
		return new ArrayBlockingQueue<T>(capacity);
	}

}
