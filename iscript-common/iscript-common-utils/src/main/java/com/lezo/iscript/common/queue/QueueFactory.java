package com.lezo.iscript.common.queue;

import java.util.Queue;

public interface QueueFactory<T> {
	Queue<T> newQueue(int capacity);
}
