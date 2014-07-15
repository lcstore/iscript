package com.lezo.iscript.yeam.server.event.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.mina.core.future.WriteFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ConfigEventHandler extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(ConfigEventHandler.class);

	@Override
	public void handle(RequestEvent event, RequestEventHandler nextHandler) {
		if (IoConstant.EVENT_TYPE_CONFIG == event.getType()) {
			// TODO: sent new config to client
			doHandle(event);
		} else {
			nextHandler.handle(event, nextHandler.getNextHandler());
		}

	}

	private void doHandle(RequestEvent event) {
		List<ConfigWritable> configWritables = new ArrayList<ConfigWritable>();
		long configStamp = 0;
		// TODO: get config stamp from session or message
		Iterator<Entry<String, ConfigWritable>> it = ConfigBuffer.getInstance().unmodifyIterator();
		while (it.hasNext()) {
			ConfigWritable config = it.next().getValue();
			if (config.getStamp() > configStamp) {
				configWritables.add(config);
			}
		}
		IoRespone ioRespone = new IoRespone();
		ioRespone.setType(event.getType());
		ioRespone.setData(configWritables);
		WriteFuture writeFuture = event.getSession().write(ioRespone);
		if (!writeFuture.awaitUninterruptibly(IoConstant.WRITE_TIMEOUT)) {
			String msg = "fail to update configs:" + configWritables.size();
			logger.warn(msg, writeFuture.getException());
		}
	}

}
