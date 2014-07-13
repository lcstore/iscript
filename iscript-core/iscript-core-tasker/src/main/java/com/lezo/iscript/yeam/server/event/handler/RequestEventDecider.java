package com.lezo.iscript.yeam.server.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.server.event.RequestEvent;

public class RequestEventDecider extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(RequestEventDecider.class);

	public RequestEventDecider(RequestEventHandler nextEventHandler) {
		super(nextEventHandler);
	}

	@Override
	public void handle(RequestEvent event, RequestEventHandler nextHandler) {
		if (IoConstant.EVENT_TYPE_NONE == event.getType()) {
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
		event.setType(IoConstant.EVENT_TYPE_CONFIG);
	}

}
