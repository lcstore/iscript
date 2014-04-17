package com.lezo.iscript.yeam.client.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.lezo.iscript.yeam.writable.TaskWritable;

public class TasksHolder {
	private static final BlockingQueue<TaskWritable> taskQueue = new LinkedBlockingQueue<TaskWritable>();

	private static class InstanceHolder {
		private static final TasksHolder instance = new TasksHolder();
	}

	public static TasksHolder getInstance() {
		return InstanceHolder.instance;
	}

	public void offer(TaskWritable task) {
		taskQueue.offer(task);
	}

	public TaskWritable poll() {
		return taskQueue.poll();
	}

	public int size() {
		return taskQueue.size();
	}

	public BlockingQueue<TaskWritable> getTaskQueue() {
		return taskQueue;
	}
}
