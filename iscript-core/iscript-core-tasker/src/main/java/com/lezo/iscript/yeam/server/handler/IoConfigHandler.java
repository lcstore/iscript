package com.lezo.iscript.yeam.server.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.HeadCacher;
import com.lezo.iscript.yeam.server.SendUtils;
import com.lezo.iscript.yeam.tasker.buffer.StampBufferHolder;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class IoConfigHandler implements MessageHandler {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(IoConfigHandler.class);
	private static final ConcurrentHashMap<String, Long> LAST_REQUEST_MAP = new ConcurrentHashMap<String, Long>();
	private static final long MIN_PERIOD_TIME = 60000;

	public void handleMessage(IoSession session, Object message) {
		IoRequest ioRequest = (IoRequest) message;
		if (ioRequest == null) {
			return;
		}
		ensureConfigLoaded();
		String header = ioRequest.getHeader();
		JSONObject hObject = JSONUtils.getJSONObject(header);
		String clientName = JSONUtils.getString(hObject, "name");
		Long lastRequest = LAST_REQUEST_MAP.get(clientName);
		if (lastRequest == null || System.currentTimeMillis() - lastRequest >= MIN_PERIOD_TIME) {
			pushConfigs(hObject, session);
			LAST_REQUEST_MAP.put(clientName, System.currentTimeMillis());
		} else {
			long cost = System.currentTimeMillis() - lastRequest;
			logger.warn("force to close client:" + clientName + ",period:" + cost + ",too often request for config.");
			session.close(true).awaitUninterruptibly();
		}
	}

	private void ensureConfigLoaded() {
		StampBeanBuffer<ConfigWritable> configBuffer = StampBufferHolder.getConfigBuffer();
		Long stamp = configBuffer.getBufferStamp();
		long timeout = 1000;
		while (stamp == 0) {
			logger.warn("wait to buffer config...");
			try {
				TimeUnit.MILLISECONDS.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stamp = configBuffer.getBufferStamp();
		}
	}

	private void pushConfigs(JSONObject hObject, IoSession ioSession) {
		Long cstamp = JSONUtils.getLong(hObject, "cstamp");
		StampBeanBuffer<ConfigWritable> configBuffer = StampBufferHolder.getConfigBuffer();
		Long stamp = configBuffer.getBufferStamp();
		if (cstamp.equals(stamp)) {
			return;
		}
		List<ConfigWritable> configWritables = new ArrayList<ConfigWritable>();
		Iterator<Entry<String, ConfigWritable>> it = configBuffer.unmodifyIterator();
		while (it.hasNext()) {
			ConfigWritable config = it.next().getValue();
			if (config.getStamp() > cstamp) {
				configWritables.add(config);
			}
		}
		IoRespone ioRespone = new IoRespone();
		ioRespone.setType(IoConstant.EVENT_TYPE_CONFIG);
		ioRespone.setData(configWritables);
		SendUtils.doSend(hObject, ioRespone, ioSession);
	}
}
