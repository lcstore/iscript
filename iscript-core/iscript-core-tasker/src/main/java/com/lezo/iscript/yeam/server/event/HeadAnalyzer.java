package com.lezo.iscript.yeam.server.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.tasker.cache.TaskQueue;
import com.lezo.iscript.yeam.writable.ConfigWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class HeadAnalyzer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(HeadAnalyzer.class);
	private static final int PER_OFFER_SIZE = 20;
	private static final int MIN_TASK_SIZE = 5;
	private String header;
	private IoSession ioSession;

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
		pushTasks(hObject);
	}

	private void pushTasks(JSONObject hObject) {
		Integer tactive = JSONUtils.getInteger(hObject, "tactive");
		Integer tsize = JSONUtils.getInteger(hObject, "tsize");
		if (tactive + tsize < MIN_TASK_SIZE) {
			offerTasks(hObject);
		}
	}

	private void offerTasks(JSONObject hObject) {
		long start = System.currentTimeMillis();
		TaskCacher taskCancher = TaskCacher.getInstance();
		List<String> typeList = taskCancher.getNotEmptyTypeList();
		List<TaskWritable> taskOffers = new ArrayList<TaskWritable>(PER_OFFER_SIZE);
		int limit = 0;
		if (!typeList.isEmpty()) {
			// shuffle type to offer task random
			// TODO: wrapper task before to push for the client
			Collections.shuffle(typeList);
			logger.info(String.format("Ready type:%s", typeList));
			int cycle = 0;
			limit = getLimitSize(typeList);
			int remain = PER_OFFER_SIZE;
			while (remain > 0 && ++cycle <= 3) {
				for (String type : typeList) {
					TaskQueue taskQueue = taskCancher.getQueue(type);
					limit = limit > remain ? remain : limit;
					synchronized (taskQueue) {
						List<TaskWritable> taskList = taskQueue.pollDecsLevel(limit);
						if (!CollectionUtils.isEmpty(taskList)) {
							remain -= taskList.size();
							taskOffers.addAll(taskList);
							if (remain < 1) {
								break;
							}
						}
					}
				}
			}
			if (!taskOffers.isEmpty()) {
				IoRespone ioRespone = new IoRespone();
				ioRespone.setType(IoConstant.EVENT_TYPE_TASK);
				ioRespone.setData(taskOffers);
				ResponeProceser.getInstance().execute(new MessageSender(hObject, ioRespone, ioSession));
			}
		}
		long cost = System.currentTimeMillis() - start;
		String msg = String.format("Offer %s task for client:%s,[tactive:%s,Largest:%s,tsize:%s](%s),cost:%s", taskOffers.size(), JSONUtils.getString(hObject, "name"), JSONUtils.getString(hObject, "tactive"), JSONUtils.getString(hObject, "tmax"), JSONUtils.getString(hObject, "tsize"), limit, cost);
		logger.info(msg);

	}

	private int getLimitSize(List<String> typeList) {
		if (typeList.size() < 3) {
			TaskCacher taskCancher = TaskCacher.getInstance();
			int total = 0;
			for (String type : typeList) {
				total += taskCancher.getQueue(type).size();
			}
			if (total < PER_OFFER_SIZE * 20) {
				return 2;
			}
		}
		int limit = PER_OFFER_SIZE / typeList.size();
		limit = limit <= 1 ? 2 : limit;
		return limit;
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
