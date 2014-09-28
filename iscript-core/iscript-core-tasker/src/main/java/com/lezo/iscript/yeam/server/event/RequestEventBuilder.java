package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

public class RequestEventBuilder {

	public static RequestEvent creatEvent(IoSession session, Object message) {
		RequestEvent event = new RequestEvent();
		event.setSession(session);
		event.setMessage(message);
		return event;
	}
}
