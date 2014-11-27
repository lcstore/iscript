package com.lezo.iscript.yeam.server.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.HeadCacher;
import com.lezo.iscript.yeam.server.SendUtils;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.tasker.cache.TaskQueue;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class IoTaskHandler implements MessageHandler {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(IoTaskHandler.class);
	private static final int PER_OFFER_SIZE = 15;
	private static final int MIN_TASK_SIZE = 5;

	@Override
	public void handleMessage(IoSession session, Object message) {
		IoRequest ioRequest = (IoRequest) message;
		if (ioRequest == null) {
			return;
		}
		ensureTaskLoaded();
		String header = ioRequest.getHeader();
		pushTasks(JSONUtils.getJSONObject(header), session);
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

	public Object pushTasks(JSONObject hObject, IoSession ioSession) {
		if (!HeadCacher.getInstace().putIfVary(IoConstant.EVENT_TYPE_TASK, hObject)) {
			return 0;
		}
		Integer tsize = JSONUtils.getInteger(hObject, "tsize");
		if (tsize >= MIN_TASK_SIZE) {
			return 0;
		}
		long start = System.currentTimeMillis();
		TaskCacher taskCancher = TaskCacher.getInstance();
		List<String> typeList = taskCancher.getNotEmptyTypeList();
		List<TaskWritable> taskOffers = new ArrayList<TaskWritable>(PER_OFFER_SIZE);
		int limit = 0;
		int sendCount = 0;
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
				SendUtils.doSend(hObject, ioRespone, ioSession);
				sendCount = taskOffers.size();
			}
		}
		long cost = System.currentTimeMillis() - start;
		String msg = String.format("Offer %s task for client:%s,[tactive:%s,Largest:%s,tsize:%s](%s),cost:%s",
				taskOffers.size(), JSONUtils.getString(hObject, "name"), JSONUtils.getString(hObject, "tactive"),
				JSONUtils.getString(hObject, "tmax"), JSONUtils.getString(hObject, "tsize"), limit, cost);
		logger.info(msg);
		return sendCount;
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

}
