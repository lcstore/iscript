package com.lezo.iscript.yeam.server.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.server.event.RequestEvent;

public class EmptyEventHandler implements RequestEventHandler {
	private static Logger logger = LoggerFactory.getLogger(EmptyEventHandler.class);

	@Override
	public void handle(RequestEvent event, RequestEventHandler nextHandler) {
		logger.warn("not handler event:" + event.getType());
	}

	@Override
	public RequestEventHandler getNextHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
