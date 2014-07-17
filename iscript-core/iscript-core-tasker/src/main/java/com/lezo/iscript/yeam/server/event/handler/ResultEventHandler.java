package com.lezo.iscript.yeam.server.event.handler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.result.ResultHandlerCaller;
import com.lezo.iscript.yeam.result.ResultHandlerWoker;
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
		if (!CollectionUtils.isEmpty(rWritables)) {
			ResultHandlerCaller caller = ResultHandlerCaller.getInstance();
			caller.execute(new ResultHandlerWoker(rWritables));
			String msg = String.format("add %d result to caller.active woker:%d,in Queue worker:%d", rWritables.size(),
					caller.getExecutor().getActiveCount(), caller.getExecutor().getQueue().size());
			logger.info(msg);
		}
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
