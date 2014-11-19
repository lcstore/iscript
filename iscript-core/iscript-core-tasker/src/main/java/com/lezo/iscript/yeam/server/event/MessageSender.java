package com.lezo.iscript.yeam.server.event;

import java.util.Collection;

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;

public class MessageSender implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(MessageSender.class);
	private JSONObject hObject;
	private IoRespone ioRespone;
	private IoSession ioSession;

	public MessageSender(JSONObject hObject, IoRespone ioRespone, IoSession ioSession) {
		super();
		this.hObject = hObject;
		this.ioRespone = ioRespone;
		this.ioSession = ioSession;
	}

	@Override
	public void run() {
		String clientName = JSONUtils.getString(hObject, "name");
		long start = System.currentTimeMillis();
		int size = getDateSize();
		WriteFuture writeFuture = ioSession.write(ioRespone);
		if (!writeFuture.awaitUninterruptibly(IoConstant.WRITE_TIMEOUT)) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("fail to push tasks to client:{},type:{},size:{},cost:{}", clientName, getResponeType(), size, cost);
			logger.warn(msg, writeFuture.getException());
		} else {
			long cost = System.currentTimeMillis() - start;
			logger.info("finish to push tasks to client:{},type:{},size:{},cost:{}", clientName, getResponeType(), size, cost);
		}
	}

	private String getResponeType() {
		String name = "unkown";
		switch (ioRespone.getType()) {
		case IoConstant.EVENT_TYPE_CONFIG: {
			name = "config";
			break;
		}
		case IoConstant.EVENT_TYPE_TASK: {
			name = "task";
			break;
		}
		default: {
			break;
		}
		}
		return name;
	}

	private int getDateSize() {
		Object dObject = ioRespone.getData();
		if (dObject instanceof Collection) {
			Collection<?> dataCollection = (Collection<?>) dObject;
			return dataCollection.size();
		}
		return 1;
	}

}
