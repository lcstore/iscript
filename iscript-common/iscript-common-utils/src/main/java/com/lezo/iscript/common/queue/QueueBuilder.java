package com.lezo.iscript.common.queue;

import java.util.Queue;

public interface QueueBuilder<T> {
	Queue<T> newQueue();
}
