package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.io.IoConstant;

public class RequestEventBuilder {

	public static RequestEvent creatEvent(IoSession session, Object message) {
		return creatEvent(session, message, IoConstant.EVENT_TYPE_NONE);
	}

	public static RequestEvent creatEvent(IoSession session, Object message, int type) {
		RequestEvent event = new RequestEvent();
		event.setType(type);
		event.setSession(session);
		event.setMessage(message);
		return event;
	}
}