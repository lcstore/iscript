package com.lezo.iscript.yeam.server.event.handler;

import com.lezo.iscript.yeam.server.event.RequestEvent;

public class RequstEventDecider implements RequestEventHandler {
	private RequestEventHandler next = new TaskEventHandler();

	@Override
	public void handle(RequestEvent event, RequestEventHandler nextHandler) {
		if (RequestEvent.TYPE_NONE == event.getType()) {
			decideType(event);
		}
		if (nextHandler == null) {
			nextHandler = getNextHandler();
		}
		nextHandler.handle(event, nextHandler.getNextHandler());
	}

	/**
	 * decide the type for this event by the data
	 * 
	 * @param event
	 */
	private void decideType(RequestEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public RequestEventHandler getNextHandler() {
		return next;
	}

}
