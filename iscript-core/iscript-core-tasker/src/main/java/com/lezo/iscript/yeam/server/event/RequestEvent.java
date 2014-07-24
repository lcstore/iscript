package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.io.IoConstant;

public class RequestEvent {
	private int type = IoConstant.EVENT_TYPE_NONE;
	private IoSession session;
	private Object message;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}
}
