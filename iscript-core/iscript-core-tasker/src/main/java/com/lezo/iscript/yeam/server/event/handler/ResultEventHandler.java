package com.lezo.iscript.yeam.server.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultEventHandler extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(ResultEventHandler.class);

	@Override
	public void handle(RequestEvent event, RequestEventHandler nextHandler) {
		if (IoConstant.EVENT_TYPE_CONFIG == event.getType()) {
			// TODO: sent new config to client
			doHandle(event);
		} else {
			nextHandler.handle(event, nextHandler.getNextHandler());
		}

	}

	private IoRequest getIoRequest(RequestEvent event) {
		Object dataObject = event.getMessage();
		if (dataObject == null) {
			return null;
		}
		if (dataObject instanceof IoRequest) {
		} else {
			logger.warn("unkonw event data object:" + dataObject.getClass().getName());
			return null;
		}
		return (IoRequest) dataObject;
	}

	private void doHandle(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null || ioRequest.getData() == null) {
			return;
		}
		Object rsObject = ioRequest.getData();
		if (rsObject == null || !(rsObject instanceof ResultWritable)) {
			return;
		}
		ResultWritable rWritable = (ResultWritable) rsObject;
		// TODO: handle result...
		logger.info("getTaskId:" + rWritable.getTaskId());
	}

}
