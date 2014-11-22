package com.lezo.iscript.yeam.server.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.session.ActionHandler;
import com.lezo.iscript.yeam.server.session.TaskActionHandler;
import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class HeadAnalyzer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(HeadAnalyzer.class);
	private String header;
	private IoSession ioSession;
	private static final ActionHandler TASK_ACTION_HANDLER = new TaskActionHandler();

	public HeadAnalyzer(String header, IoSession ioSession) {
		super();
		this.header = header;
		this.ioSession = ioSession;
	}

	@Override
	public void run() {
		ensureConfigLoaded();
		JSONObject hObject = JSONUtils.getJSONObject(header);
		pushConfigs(hObject);
		ensureTaskLoaded();
		pushTasks(hObject);
	}

	private void ensureTaskLoaded() {
		TaskCacher taskCacher = TaskCacher.getInstance();
		while (taskCacher.getTypeList().isEmpty()) {
			logger.warn("wait to buffer tasks...");
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void pushTasks(JSONObject hObject) {
		TASK_ACTION_HANDLER.callAction(hObject, ioSession);
	}

	private void pushConfigs(JSONObject hObject) {
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
		ResponeProceser.getInstance().execute(new MessageSender(hObject, ioRespone, ioSession));
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

}
