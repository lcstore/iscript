package com.lezo.iscript.yeam.client.result;

import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskResult {
	private TaskWritable task;
	private ResultWritable result;

	public TaskWritable getTask() {
		return task;
	}

	public void setTask(TaskWritable task) {
		this.task = task;
	}

	public ResultWritable getResult() {
		return result;
	}

	public void setResult(ResultWritable result) {
		this.result = result;
	}

}
