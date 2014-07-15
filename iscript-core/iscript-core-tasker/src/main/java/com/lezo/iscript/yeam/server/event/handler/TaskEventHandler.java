package com.lezo.iscript.yeam.server.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.server.event.RequestEvent;

public class TaskEventHandler extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(TaskEventHandler.class);

	@Override
	public void handle(RequestEvent event, RequestEventHandler nextHandler) {
		if (IoConstant.EVENT_TYPE_TASK == event.getType()) {
			// TODO: sent new tasks to client
		} else {
			nextHandler.handle(event, nextHandler.getNextHandler());
		}

	}

}
