package com.lezo.iscript.yeam.client.listener;

import com.lezo.iscript.yeam.client.event.CallEvent;

public class DoingTaskListener extends AbstractCallEventListener {

	@Override
	protected void doCallEvent(CallEvent event) {
	}

	@Override
	protected int getType() {
		return CallEvent.DOING_TASK_EVENT;
	}

}
