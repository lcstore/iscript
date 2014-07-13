package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.server.event.handler.RequestEventHandler;
import com.lezo.iscript.yeam.server.event.handler.RequstEventDecider;

public class RequestEventWorker implements Runnable {
	private RequestEventHandler firstHandler = new RequstEventDecider();
	private IoSession session;
	private Object message;

	public RequestEventWorker(IoSession session, Object message) {
		super();
		this.session = session;
		this.message = message;
	}

	@Override
	public void run() {
		// TODO: record this request
		RequestEvent event = RequestEventBuilder.creatEvent(session, message);
		firstHandler.handle(event, firstHandler.getNextHandler());
	}
}
