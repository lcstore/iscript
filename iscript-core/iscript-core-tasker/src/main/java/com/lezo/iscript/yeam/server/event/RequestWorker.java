package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.server.event.handler.RequestEventNotifyer;

public class RequestWorker implements Runnable {
	private IoSession session;
	private Object message;

	public RequestWorker(IoSession session, Object message) {
		super();
		this.session = session;
		this.message = message;
	}

	@Override
	public void run() {
		// TODO: record this request
		RequestEvent event = RequestEventBuilder.creatEvent(session, message);
		RequestEventNotifyer.notifyEvent(event);
	}
}
