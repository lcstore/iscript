package com.lezo.iscript.yeam.server.event.handler;

import com.lezo.iscript.yeam.server.event.RequestEvent;

public interface RequestEventHandler {
	void handle(RequestEvent event, RequestEventHandler nextHandler);

	RequestEventHandler getNextHandler();
}
