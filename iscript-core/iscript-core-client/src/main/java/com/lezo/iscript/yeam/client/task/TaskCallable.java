package com.lezo.iscript.yeam.client.task;

import java.util.concurrent.Callable;

import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.client.event.CallEvent;
import com.lezo.iscript.yeam.client.event.EventManager;
import com.lezo.iscript.yeam.client.result.TaskResult;
import com.lezo.iscript.yeam.service.TaskCallalbeService;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskCallable implements Callable<ResultWritable> {
	private TaskWritable task;

	public TaskCallable(TaskWritable task) {
		super();
		this.task = task;
	}

	@Override
	public ResultWritable call() throws Exception {
		beforeCall(task);
		TaskCallalbeService taskCallableService = (TaskCallalbeService) ObjectBuilder.newObject(
				TaskCallalbeService.class, true);
		ResultWritable result = taskCallableService.doCall(task);
		TaskResult taskResult = new TaskResult();
		taskResult.setTask(task);
		taskResult.setResult(result);
		afterCall(taskResult);
		return result;
	}

	private void beforeCall(Object obj) {
		EventManager.getInstance().notifyEvent(new CallEvent(obj, CallEvent.DOING_TASK_EVENT));
	}

	private void afterCall(Object obj) {
		EventManager.getInstance().notifyEvent(new CallEvent(obj, CallEvent.DONE_TASK_EVENT));
	}

}
