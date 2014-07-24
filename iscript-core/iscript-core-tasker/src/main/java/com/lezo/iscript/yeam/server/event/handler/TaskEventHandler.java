package com.lezo.iscript.yeam.server.event.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.future.WriteFuture;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.tasker.cache.TaskQueue;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskEventHandler extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(TaskEventHandler.class);
	private static final int PER_OFFER_SIZE = 10;
	private static final int MIN_TASK_SIZE = 4;

	@Override
	protected void doHandle(RequestEvent event) {
		long start = System.currentTimeMillis();
		IoRequest request = getIoRequest(event);
		JSONObject hObject = JSONUtils.getJSONObject(request.getHeader());

		TaskCacher taskCancher = TaskCacher.getInstance();
		List<String> typeList = taskCancher.getTypeList();
		List<TaskWritable> taskOffers = new ArrayList<TaskWritable>(PER_OFFER_SIZE);
		if (!typeList.isEmpty()) {
			// shuffle type to offer task random
			Collections.shuffle(typeList);
			int cycle = 0;
			int limit = 2;
			int remain = PER_OFFER_SIZE;
			while (remain > 0 && ++cycle <= 3) {
				for (String type : typeList) {
					TaskQueue taskQueue = taskCancher.getQueue(type);
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
				WriteFuture writeFuture = event.getSession().write(ioRespone);
				if (!writeFuture.awaitUninterruptibly(IoConstant.WRITE_TIMEOUT)) {
					String msg = "fail to offer tasks:" + taskOffers.size();
					logger.warn(msg, writeFuture.getException());
				}
			}
		}
		long cost = System.currentTimeMillis() - start;
		String msg = String.format("Offer %d task for client:%s,cost:%s", taskOffers.size(),
				JSONUtils.getString(hObject, "name"), cost);
		logger.info(msg);

	}

	@Override
	protected boolean isAccept(RequestEvent event) {
		if (IoConstant.EVENT_TYPE_CONFIG == event.getType()) {
			return false;
		}
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null) {
			return false;
		}
		JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
		if (hObject == null) {
			logger.warn("get an empty header..");
			return false;
		}
		Integer tactive = JSONUtils.getInteger(hObject, "tactive");
		Integer tsize = JSONUtils.getInteger(hObject, "tsize");
		if (tactive + tsize < MIN_TASK_SIZE) {
			return true;
		}
		return false;
	}
}
