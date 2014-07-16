package com.lezo.iscript.yeam.server.event.handler;

import java.util.ArrayList;
import java.util.List;

import com.lezo.iscript.yeam.server.event.RequestEvent;

public class RequestEventNotifyer {
	private static final List<RequestEventHandler> EVENT_HANDLER_LIST = new ArrayList<RequestEventHandler>();
	static {
		EVENT_HANDLER_LIST.add(new ConfigEventHandler());
		EVENT_HANDLER_LIST.add(new TaskEventHandler());
		EVENT_HANDLER_LIST.add(new ResultEventHandler());
		EVENT_HANDLER_LIST.add(new EmptyEventHandler());
	}

	public static void notifyEvent(RequestEvent event) {
		for (RequestEventHandler handler : EVENT_HANDLER_LIST) {
			handler.handle(event);
		}
	}

}
