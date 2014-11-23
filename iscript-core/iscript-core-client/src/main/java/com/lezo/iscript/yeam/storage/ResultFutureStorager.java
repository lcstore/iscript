package com.lezo.iscript.yeam.storage;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.mina.SessionSender;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultFutureStorager implements StorageListener<Future<ResultWritable>> {
	private static Logger logger = LoggerFactory.getLogger(ResultFutureStorager.class);
	private static final int capacity = 200;
	private StorageBuffer<Future<ResultWritable>> storageBuffer = new StorageBuffer<Future<ResultWritable>>(capacity);
	private static ResultFutureStorager instance;

	private ResultFutureStorager() {
	}

	public static ResultFutureStorager getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (ResultFutureStorager.class) {
			if (instance == null) {
				instance = new ResultFutureStorager();
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
		StorageCaller.getInstance().execute(new ResultFutureClassifier(copyList));
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
