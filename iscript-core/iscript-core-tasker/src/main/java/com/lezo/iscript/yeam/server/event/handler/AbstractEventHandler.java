package com.lezo.iscript.yeam.server.event.handler;


public abstract class AbstractEventHandler implements RequestEventHandler {
	private RequestEventHandler nextEventHandler;

	public AbstractEventHandler(RequestEventHandler nextEventHandler) {
		super();
		this.nextEventHandler = nextEventHandler;
	}


	@Override
	public RequestEventHandler getNextHandler() {
		return this.nextEventHandler;
	}

}
