package com.lezo.iscript.yeam.server.event.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultEventHandler extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(ResultEventHandler.class);

	protected void doHandle(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null || ioRequest.getData() == null) {
			return;
		}
		Object rsObject = ioRequest.getData();
		@SuppressWarnings("unchecked")
		List<ResultWritable> rWritables = (List<ResultWritable>) rsObject;
		for (ResultWritable rWritable : rWritables) {
			logger.info("id:" + rWritable.getTaskId() + ",rs:" + rWritable.getResult());
		}
		// TODO: handle result...
	}

	@Override
	protected boolean isAccept(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null) {
			return false;
		}
		Object dataObject = ioRequest.getData();
		if (dataObject == null) {
			return false;
		}
		if (dataObject instanceof List) {
			return true;
		}
		return false;
	}
}
