package com.lezo.iscript.yeam.server.event.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.mina.core.future.WriteFuture;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ConfigEventHandler extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(ConfigEventHandler.class);

	protected void doHandle(RequestEvent event) {
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
		ioRespone.setType(IoConstant.EVENT_TYPE_CONFIG);
		ioRespone.setData(configWritables);
		WriteFuture writeFuture = event.getSession().write(ioRespone);
		if (!writeFuture.awaitUninterruptibly(IoConstant.WRITE_TIMEOUT)) {
			String msg = "fail to update configs:" + configWritables.size();
			logger.warn(msg, writeFuture.getException());
		}
	}

	@Override
	protected boolean isAccept(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null) {
			return false;
		}
		JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
		if (hObject == null) {
			logger.warn("get an empty header..");
			return false;
		}
		Long cstamp = JSONUtils.getLong(hObject, "cstamp");
		if (cstamp == null || cstamp < ConfigBuffer.getInstance().getStamp()) {
			return true;
		}
		return false;
	}

}
