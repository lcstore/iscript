package com.lezo.iscript.yeam.server;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.server.handler.IoConfigHandler;
import com.lezo.iscript.yeam.server.handler.IoResultHandler;
import com.lezo.iscript.yeam.server.handler.IoTaskHandler;
import com.lezo.iscript.yeam.server.handler.IoTokenHandler;
import com.lezo.iscript.yeam.server.handler.MessageHandler;
import com.lezo.iscript.yeam.tasker.buffer.StampBufferHolder;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ClientEventDispatcher {
	private static final int MIN_TASK_SIZE = 5;

	public void fireEvent(IoSession session, IoRequest ioRequest) {
		if (ioRequest == null) {
			return;
		}
		MessageHandler handler = null;
		switch (ioRequest.getType()) {
		case IoRequest.REQUEST_REPORT: {
			handler = createHander(ioRequest);
			break;
		}
		case IoRequest.REQUEST_RESULT: {
			handler = new IoResultHandler();
			break;
		}
		default:
			break;
		}
		if (handler != null) {
			handler.handleMessage(session, ioRequest);
		}
	}

	private MessageHandler createHander(IoRequest ioRequest) {
		JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
		if (hObject == null) {
			return null;
		}
		Long tokenStamp = JSONUtils.getLong(hObject, "tokenStamp");
		tokenStamp = tokenStamp == null ? 0 : tokenStamp;
		StampBeanBuffer<ClientTokenDto> tokenBuffer = StampBufferHolder.getClientTokenBuffer();
		Long bufferStamp = tokenBuffer.getBufferStamp();
		if (!bufferStamp.equals(tokenStamp)) {
			return bufferStamp > 0 ? new IoTokenHandler() : null;
		}
		Long cstamp = JSONUtils.getLong(hObject, "cstamp");
		StampBeanBuffer<ConfigWritable> configBuffer = StampBufferHolder.getConfigBuffer();
		Long stamp = configBuffer.getBufferStamp();
		if (!cstamp.equals(stamp)) {
			return stamp > 0 ? new IoConfigHandler() : null;
		}
		Integer tsize = JSONUtils.getInteger(hObject, "tsize");
		if (tsize < MIN_TASK_SIZE) {
			return TaskCacher.getInstance().getTypeCount() > 0 ? new IoTaskHandler() : null;
		}
		return null;
	}
}
