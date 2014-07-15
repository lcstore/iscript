package com.lezo.iscript.yeam.server.event.handler;

public abstract class AbstractEventHandler implements RequestEventHandler {
	private RequestEventHandler nextEventHandler;

	@Override
	public RequestEventHandler getNextHandler() {
		return this.nextEventHandler;
	}

	public void setNextEventHandler(RequestEventHandler nextEventHandler) {
		this.nextEventHandler = nextEventHandler;
	}

}
