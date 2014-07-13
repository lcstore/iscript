package com.lezo.iscript.yeam.server.event.handler;

import com.lezo.iscript.yeam.server.event.RequestEvent;

public class TaskEventHandler implements RequestEventHandler {

	@Override
	public void handle(RequestEvent event, RequestEventHandler nextHandler) {
		if (RequestEvent.TYPE_TASK == event.getType()) {
			// TODO: sent new tasks to client
		} else {
			nextHandler.handle(event, nextHandler.getNextHandler());
		}

	}

	@Override
	public RequestEventHandler getNextHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
