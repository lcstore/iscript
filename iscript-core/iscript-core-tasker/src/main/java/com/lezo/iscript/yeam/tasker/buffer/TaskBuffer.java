package com.lezo.iscript.yeam.tasker.buffer;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskBuffer {
	private static final TaskBuffer INSTANCE = new TaskBuffer();
	private int capacity = 100;
	private final Queue<List<TaskWritable>> packQueue = new ArrayBlockingQueue<List<TaskWritable>>(capacity);

	private TaskBuffer() {
	}

	public static TaskBuffer getInstance() {
		return INSTANCE;
	}

	public boolean offer(List<TaskWritable> taskList) {
		if (CollectionUtils.isEmpty(taskList)) {
			return false;
		}
		return packQueue.offer(taskList);
	}

	public List<TaskWritable> poll() {
		return packQueue.poll();
	}

	public Queue<List<TaskWritable>> getPackQueue() {
		return packQueue;
	}

}
