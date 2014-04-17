package com.lezo.iscript.yeam.tasker.cache;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskCacher {
	private static final TaskCacher INSTANCE = new TaskCacher();
	private ConcurrentHashMap<String, Queue<TaskWritable>> taskQueueMap = new ConcurrentHashMap<String, Queue<TaskWritable>>();

	private TaskCacher() {
	}

	public static TaskCacher getInstance() {
		return INSTANCE;
	}

	public Queue<TaskWritable> getQueue(String type) {
		Queue<TaskWritable> queue = taskQueueMap.get(type);
		if (queue == null) {
			synchronized (taskQueueMap) {
				queue = taskQueueMap.get(type);
				if (queue == null) {
					queue = new LinkedBlockingQueue<TaskWritable>();
					taskQueueMap.put(type, queue);
				}
			}
		}
		return queue;
	}

	public ConcurrentHashMap<String, Queue<TaskWritable>> getTypeQueueMap() {
		return taskQueueMap;
	}
}
