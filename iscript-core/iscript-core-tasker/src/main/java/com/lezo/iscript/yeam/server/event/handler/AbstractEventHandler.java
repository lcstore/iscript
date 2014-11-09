package com.lezo.iscript.yeam.server.event.handler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.server.event.ClientEvent;

public abstract class AbstractEventHandler implements ClientEventHandler {
	private static Logger logger = LoggerFactory.getLogger(AbstractEventHandler.class);
	private ClientEventHandler nextHandler = new EmptyEventHandler();

	@Override
	public void handle(ClientEvent event) {
		try {
			if (!isAccept(event)) {
				if (nextHandler != null) {
					nextHandler.handle(event);
				}
				return;
			}
			doHandle(event);
		} catch (Exception e) {
			String msg = "Cause from " + this.getClass().getName() + ",\n" + ExceptionUtils.getStackTrace(e);
			logger.warn(msg);
		}
	}

	protected abstract void doHandle(ClientEvent event);

	protected boolean isAccept(ClientEvent event) {
		return true;
	}

	protected final IoRequest getIoRequest(ClientEvent event) {
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

	public void setNextHandler(ClientEventHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

}
