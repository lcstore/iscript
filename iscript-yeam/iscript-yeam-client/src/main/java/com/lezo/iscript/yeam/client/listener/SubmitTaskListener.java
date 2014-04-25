package com.lezo.iscript.yeam.client.listener;

import java.util.concurrent.ThreadPoolExecutor;

import com.lezo.iscript.yeam.client.event.CallEvent;
import com.lezo.iscript.yeam.client.task.TaskCallable;
import com.lezo.iscript.yeam.client.task.TasksCaller;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class SubmitTaskListener extends AbstractCallEventListener {
	private ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();

	@Override
	protected void doCallEvent(CallEvent event) {
		Object rwObject = event.getSource();
		if (!(rwObject instanceof TaskWritable)) {
			return;
		}
		TaskWritable task = (TaskWritable) rwObject;
		caller.submit(new TaskCallable(task));
	}

	@Override
	protected int getType() {
		return CallEvent.SUBMIT_TASK_EVENT;
	}

}
