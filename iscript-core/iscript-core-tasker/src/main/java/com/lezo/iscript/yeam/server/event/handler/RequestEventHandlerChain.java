package com.lezo.iscript.yeam.server.event.handler;

public class RequestEventHandlerChain {

	public static AbstractEventHandler getDefaultChain() {
		RequestEventDecider requestEventDecider = new RequestEventDecider();
		ConfigEventHandler configEventHandler = new ConfigEventHandler();
		TaskEventHandler taskEventHandler = new TaskEventHandler();
		ResultEventHandler resultEventHandler = new ResultEventHandler();
		EmptyEventHandler emptyEventHandler = new EmptyEventHandler();
		requestEventDecider.setNextEventHandler(configEventHandler);
		configEventHandler.setNextEventHandler(taskEventHandler);
		taskEventHandler.setNextEventHandler(resultEventHandler);
		resultEventHandler.setNextEventHandler(emptyEventHandler);
		return requestEventDecider;
	}

}
