package com.lezo.iscript.common.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;

public class QueueContianer {
	private static QueueContianer instance;
	private ConcurrentHashMap<Class<?>, Queue<?>> queueMap = new ConcurrentHashMap<Class<?>, Queue<?>>();

	private QueueContianer() {
	}

	public static QueueContianer getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (QueueContianer.class) {
			if (instance == null) {
				instance = new QueueContianer();
			}
		}
		return instance;
	}

	public <T> void addQueue(Class<T> taskClass, Queue<T> queue) {
		queueMap.put(taskClass, queue);
	}

	@SuppressWarnings("unchecked")
	public <T> Queue<T> getQueue(Class<T> taskClass) {
		return (Queue<T>) queueMap.get(taskClass);
	}

	public <T> boolean offer(List<T> elementList, QueueBuilder<T> builder) {
		if (CollectionUtils.isEmpty(elementList)) {
			return false;
		}
		T firstEle = elementList.get(0);
		@SuppressWarnings("unchecked")
		Class<T> taskClass = (Class<T>) firstEle.getClass();
		Queue<T> taskQueue = getQueue(taskClass);
		if (taskQueue == null) {
			synchronized (queueMap) {
				if (taskQueue == null) {
					taskQueue = builder.newQueue();
					queueMap.put(firstEle.getClass(), taskQueue);
				}
			}
		}

		for (T element : elementList) {
			if (!taskQueue.offer(element)) {
				return false;
			}
		}
		return true;
	}

	public <T> List<T> poll(Class<T> taskClass, int limit) {
		Queue<T> queue = getQueue(taskClass);
		if (queue == null) {
			return Collections.emptyList();
		}
		List<T> taskList = new ArrayList<T>(limit);
		while (limit-- > 0) {
			T task = queue.poll();
			if (task == null) {
				break;
			}
			taskList.add(task);
		}
		return taskList;
	}
}
