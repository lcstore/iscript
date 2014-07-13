package com.lezo.iscript.yeam.server.event;

import org.apache.mina.core.session.IoSession;

public class RequestEvent {
	public static final int TYPE_NONE = 0;
	public static final int TYPE_CONFIG = 1;
	public static final int TYPE_TASK = 2;
	private int type = TYPE_NONE;
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
