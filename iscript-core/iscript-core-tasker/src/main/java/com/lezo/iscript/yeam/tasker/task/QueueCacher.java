package com.lezo.iscript.yeam.tasker.task;

public interface QueueCacher<E> {
	void offer(E e);

	void poll(int limit);
}
