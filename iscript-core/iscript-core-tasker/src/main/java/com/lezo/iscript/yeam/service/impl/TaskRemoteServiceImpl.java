package com.lezo.iscript.yeam.service.impl;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.lezo.iscript.common.queue.QueueBuilder;
import com.lezo.iscript.common.queue.QueueContianer;
import com.lezo.iscript.yeam.service.TaskRemoteService;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskRemoteServiceImpl implements TaskRemoteService {
	private QueueBuilder<TaskWritable> builder = new QueueBuilder<TaskWritable>() {
		@Override
		public Queue<TaskWritable> newQueue() {
			int capacity = 1000;
			return new ArrayBlockingQueue<TaskWritable>(capacity);
		}
	};

	@Override
	public void addTasks(List<TaskWritable> taskList) throws IllegalArgumentException {
		QueueContianer.getInstance().offer(taskList, builder);
	}

	@Override
	public int updateTasks(List<Long> taskIds, int status, String handler) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

}
