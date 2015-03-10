package com.lezo.iscript.yeam.server;

import java.util.Collection;

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;

public class SendUtils {
	private static Logger logger = LoggerFactory.getLogger(SendUtils.class);

	public static void doSend(JSONObject hObject, IoRespone ioRespone, IoSession ioSession) {
		String clientName = JSONUtils.getString(hObject, "name");
		long start = System.currentTimeMillis();
		int size = getDateSize(ioRespone);
		WriteFuture writeFuture = ioSession.write(ioRespone);
		if (!writeFuture.awaitUninterruptibly(IoConstant.WRITE_TIMEOUT)) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("fail to push {}s to client:{},size:{},cost:{}", getResponeType(ioRespone), clientName, size, cost);
			logger.warn(msg, writeFuture.getException());
		} else {
			long cost = System.currentTimeMillis() - start;
			logger.info("finish to push {}s to client:{},size:{},cost:{}", getResponeType(ioRespone), clientName, size, cost);
		}
	}

	private static String getResponeType(IoRespone ioRespone) {
		String name = "unkown";
		switch (ioRespone.getType()) {
		case IoConstant.EVENT_TYPE_CONFIG: {
			name = "config";
			break;
		}
		case IoConstant.EVENT_TYPE_PROXY: {
			name = "proxy";
			break;
		}
		case IoConstant.EVENT_TYPE_TASK: {
			name = "task";
			break;
		}
		case IoConstant.EVENT_TYPE_TOKEN: {
			name = "token";
			break;
		}
		default: {
			break;
		}
		}
		return name;
	}

	private static int getDateSize(IoRespone ioRespone) {
		Object dObject = ioRespone.getData();
		if (dObject instanceof Collection) {
			Collection<?> dataCollection = (Collection<?>) dObject;
			return dataCollection.size();
		}
		return 1;
	}
}
