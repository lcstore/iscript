package com.lezo.iscript.yeam.client.listener;

import java.util.concurrent.BlockingQueue;

import com.lezo.iscript.yeam.client.event.CallEvent;
import com.lezo.iscript.yeam.client.task.TasksHolder;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class FetchTaskListener extends AbstractCallEventListener {
	private BlockingQueue<TaskWritable> taskQueue = TasksHolder.getInstance().getTaskQueue();

	@Override
	protected void doCallEvent(CallEvent event) {
		TaskWritable taskWritable = (TaskWritable) event.getSource();
		taskQueue.offer(taskWritable);
	}

	@Override
	protected int getType() {
		return CallEvent.FETCH_TASK_EVENT;
	}

}
