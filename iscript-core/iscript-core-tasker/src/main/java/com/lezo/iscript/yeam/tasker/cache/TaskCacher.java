package com.lezo.iscript.yeam.tasker.cache;

import java.util.concurrent.ConcurrentHashMap;

public class TaskCacher {
	private static final TaskCacher INSTANCE = new TaskCacher();
	private ConcurrentHashMap<String, TaskQueue> taskQueueMap = new ConcurrentHashMap<String, TaskQueue>();

	private TaskCacher() {
	}

	public static TaskCacher getInstance() {
		return INSTANCE;
	}

	public TaskQueue getQueue(String type) {
		TaskQueue queue = taskQueueMap.get(type);
		if (queue == null) {
			synchronized (taskQueueMap) {
				queue = taskQueueMap.get(type);
				if (queue == null) {
					queue = new TaskQueue(type);
					taskQueueMap.put(type, queue);
				}
			}
		}
		return queue;
	}

	public ConcurrentHashMap<String, TaskQueue> getTypeQueueMap() {
		return taskQueueMap;
	}
}
