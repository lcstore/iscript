package com.lezo.iscript.yeam.server.event.handler;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;

public class RequestEventDecider extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(RequestEventDecider.class);

	public RequestEventDecider(RequestEventHandler nextEventHandler) {
		super(nextEventHandler);
	}

	@Override
	public void handle(RequestEvent event, RequestEventHandler nextHandler) {
		handleResult(event);
		if (IoConstant.EVENT_TYPE_NONE == event.getType()) {
			decideType(event);
		}
		if (nextHandler == null) {
			nextHandler = getNextHandler();
		}
		nextHandler.handle(event, nextHandler.getNextHandler());
	}

	private void handleResult(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null || ioRequest.getData() == null) {
			return;
		}
		// TODO: handle result...
	}

	/**
	 * decide the type for this event by the data
	 * 
	 * @param event
	 */
	private void decideType(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null) {
			return;
		}
		JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
		Long cstamp = JSONUtils.get(hObject, "cstamp");
		if (cstamp == null || cstamp < ConfigBuffer.getInstance().getStamp()) {
			event.setType(IoConstant.EVENT_TYPE_CONFIG);
		} else {
			event.setType(IoConstant.EVENT_TYPE_NONE);
		}
	}

	public IoRequest getIoRequest(RequestEvent event) {
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
}
