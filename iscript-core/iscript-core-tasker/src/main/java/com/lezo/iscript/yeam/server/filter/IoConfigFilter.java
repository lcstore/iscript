package com.lezo.iscript.yeam.server.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.SendUtils;
import com.lezo.iscript.yeam.server.event.MessageSender;
import com.lezo.iscript.yeam.server.event.ResponeProceser;
import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class IoConfigFilter extends IoFilterAdapter {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(IoConfigFilter.class);

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		handleMessage(session, message);
		super.messageReceived(nextFilter, session, message);
	}

	private void handleMessage(IoSession session, Object message) {
		IoRequest ioRequest = (IoRequest) message;
		if (ioRequest == null) {
			return;
		}
		ensureConfigLoaded();
		String header = ioRequest.getHeader();
		pushConfigs(JSONUtils.getJSONObject(header), session);
	}

	private void ensureConfigLoaded() {
		Long stamp = ConfigBuffer.getInstance().getStamp();
		long timeout = 1000;
		while (stamp == 0) {
			logger.warn("wait to buffer config...");
			try {
				TimeUnit.MILLISECONDS.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stamp = ConfigBuffer.getInstance().getStamp();
		}
	}

	private void pushConfigs(JSONObject hObject, IoSession ioSession) {
		Long cstamp = JSONUtils.getLong(hObject, "cstamp");
		Long stamp = ConfigBuffer.getInstance().getStamp();
		if (cstamp.equals(stamp)) {
			return;
		}
		List<ConfigWritable> configWritables = new ArrayList<ConfigWritable>();
		Iterator<Entry<String, ConfigWritable>> it = ConfigBuffer.getInstance().unmodifyIterator();
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

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		// TODO Auto-generated method stub
		super.messageSent(nextFilter, session, writeRequest);
	}

}
