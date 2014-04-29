package com.lezo.iscript.yeam.tasker.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class QueueBuilder {
	private ConcurrentHashMap<Class<?>, Queue<Runnable>> queueMap = new ConcurrentHashMap<Class<?>, Queue<Runnable>>();

	public Queue<Runnable> getQueue(Class<? extends Runnable> taskClass) {
		return queueMap.get(taskClass);
	}

	public boolean offer(Runnable element) {
		Queue<Runnable> taskQueue = getQueue(element.getClass());
		if (taskQueue == null) {
			synchronized (queueMap) {
				if (taskQueue == null) {
					int capacity = 1000;
					taskQueue = new ArrayBlockingQueue<Runnable>(capacity);
					queueMap.put(element.getClass(), taskQueue);
				}
			}
		}
		return taskQueue.offer(element);
	}

	public List<Runnable> poll(Class<? extends Runnable> taskClass, int limit) {
		Queue<Runnable> queue = getQueue(taskClass);
		if (queue == null) {
			return Collections.emptyList();
		}
		List<Runnable> taskList = new ArrayList<Runnable>(limit);
		while (limit-- > 0) {
			Runnable task = queue.poll();
			if (task == null) {
				break;
			}
			taskList.add(task);
		}
		return taskList;
	}
}
