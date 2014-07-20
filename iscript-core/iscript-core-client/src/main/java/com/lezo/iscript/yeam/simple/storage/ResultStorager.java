package com.lezo.iscript.yeam.simple.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.simple.SessionSender;
import com.lezo.iscript.yeam.simple.utils.HeaderUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultStorager implements StorageListener<Future<ResultWritable>> {
	private static Logger logger = LoggerFactory.getLogger(ResultStorager.class);
	private static final int capacity = 200;
	private StorageBuffer<Future<ResultWritable>> storageBuffer = new StorageBuffer<Future<ResultWritable>>(capacity);
	private static ResultStorager instance;

	private ResultStorager() {
	}

	public static ResultStorager getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (ResultStorager.class) {
			if (instance == null) {
				instance = new ResultStorager();
			}
		}
		return instance;
	}

	@Override
	public void doStorage() {
		final List<Future<ResultWritable>> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			logger.info("No result to commit,send header to server.");
			sendHeader();
			return;
		}
		StorageCaller.getInstance().execute(new SendResultWorker(copyList));
	}

	private void sendHeader() {
		IoRequest request = new IoRequest();
		request.setHeader(HeaderUtils.getHeader().toString());
		SessionSender.getInstance().send(request);
	}

	public StorageBuffer<Future<ResultWritable>> getStorageBuffer() {
		return storageBuffer;
	}
}
