package com.lezo.iscript.yeam.server.event.handler;

import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.server.event.RequestEvent;

public abstract class AbstractEventHandler implements RequestEventHandler {
	@Override
	public void handle(RequestEvent event) {
		if (!isAccept(event)) {
			return;
		}
		doHandle(event);
	}

	protected abstract void doHandle(RequestEvent event);

	protected boolean isAccept(RequestEvent event) {
		return true;
	}

	protected final IoRequest getIoRequest(RequestEvent event) {
		Object dataObject = event.getMessage();
		if (dataObject == null) {
			return null;
		}
		if (dataObject instanceof IoRequest) {
		} else {
			return null;
		}
		return (IoRequest) dataObject;
	}

}
