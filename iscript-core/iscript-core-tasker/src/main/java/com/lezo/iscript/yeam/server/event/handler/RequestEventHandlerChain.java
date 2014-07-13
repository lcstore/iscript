package com.lezo.iscript.yeam.server.event.handler;

public class RequestEventHandlerChain {
	public RequestEventHandler first;
	public RequestEventHandler last;
	public RequestEventHandler current;

	public void addEventHandler(RequestEventHandler handler) {
		if (this.first == null) {
			this.last = this.first = handler;
		} else {
			this.last = handler;
		}
		this.current = handler;
	}

	public static RequestEventHandlerChain getDefaultChain() {
		RequestEventHandlerChain chain = new RequestEventHandlerChain();
		EmptyEventHandler emptyEventHandler = new EmptyEventHandler();
		TaskEventHandler taskEventHandler = new TaskEventHandler(emptyEventHandler);
		ConfigEventHandler configEventHandler = new ConfigEventHandler(taskEventHandler);
		chain.addEventHandler(new RequestEventDecider(configEventHandler));
		chain.addEventHandler(new RequestEventDecider(configEventHandler));
		chain.addEventHandler(new RequestEventDecider(taskEventHandler));
		chain.addEventHandler(new RequestEventDecider(emptyEventHandler));
		return chain;
	}

}
