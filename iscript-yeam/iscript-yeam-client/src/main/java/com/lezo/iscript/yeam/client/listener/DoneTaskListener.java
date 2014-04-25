package com.lezo.iscript.yeam.client.listener;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.client.event.CallEvent;
import com.lezo.iscript.yeam.client.result.ResultsHolder;
import com.lezo.iscript.yeam.client.result.TaskResult;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class DoneTaskListener extends AbstractCallEventListener {

	@Override
	protected void doCallEvent(CallEvent event) {
		TaskResult taskResult = (TaskResult) event.getSource();
		TaskWritable task = taskResult.getTask();
		String resulter = (String) task.get(ClientConstant.CLIENT_RESULTER_HOST);
		ResultsHolder.getInstance().addResult(resulter, taskResult.getResult());
	}

	@Override
	protected int getType() {
		return CallEvent.DONE_TASK_EVENT;
	}

}
