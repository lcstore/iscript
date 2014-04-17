package com.lezo.iscript.yeam.client.task;

import java.util.concurrent.Callable;

import com.lezo.iscript.yeam.ObjectBuilder;
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
		TaskCallalbeService taskCallableService = (TaskCallalbeService) ObjectBuilder.newObject(TaskCallalbeService.class, true);
		return taskCallableService.doCall(task);
	}

}
