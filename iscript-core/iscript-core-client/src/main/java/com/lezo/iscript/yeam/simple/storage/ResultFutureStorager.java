package com.lezo.iscript.yeam.simple.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.simple.SessionSender;
import com.lezo.iscript.yeam.simple.utils.HeaderUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultFutureStorager implements StorageListener<Future<ResultWritable>> {
	private static Logger logger = LoggerFactory.getLogger(ResultFutureStorager.class);
	private static final int capacity = 200;
	private StorageBuffer<Future<ResultWritable>> storageBuffer = new StorageBuffer<Future<ResultWritable>>(capacity);

	@Override
	public void doStorage() {
		final List<Future<ResultWritable>> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			return;
		}
		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				List<Future<ResultWritable>> waitList = sendDone(copyList);
				sendWait(waitList);
			}

		});
	}

	private void sendWait(List<Future<ResultWritable>> waitList) {
		if (!waitList.isEmpty()) {
			long start = System.currentTimeMillis();
			logger.info("start to send wait result..");
			List<ResultWritable> rwList = new ArrayList<ResultWritable>(waitList.size());
			for (Future<ResultWritable> rsf : waitList) {
				try {
					ResultWritable rw = rsf.get();
					rwList.add(rw);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			long cost = System.currentTimeMillis() - start;
			logger.info("finish to send wait result:" + waitList.size() + ",cost:" + cost);
		}

	}

	private List<Future<ResultWritable>> sendDone(List<Future<ResultWritable>> copyList) {
		long start = System.currentTimeMillis();
		logger.info("start to send done result..");
		List<Future<ResultWritable>> waitList = new ArrayList<Future<ResultWritable>>(copyList.size());
		List<ResultWritable> rwList = new ArrayList<ResultWritable>(copyList.size());
		for (Future<ResultWritable> rsf : copyList) {
			if (rsf.isDone()) {
				try {
					ResultWritable rw = rsf.get();
					rwList.add(rw);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else {
				waitList.add(rsf);
			}
		}
		sendRequest(rwList);
		long cost = System.currentTimeMillis() - start;
		logger.info("finish to send done result:" + rwList.size() + ",wait:" + waitList.size() + ",cost:" + cost);
		return waitList;
	}

	private void sendRequest(List<ResultWritable> rwList) {
		if (!rwList.isEmpty()) {
			IoRequest ioRequest = new IoRequest();
			JSONObject hObject = HeaderUtils.getHeader();
			ioRequest.setHeader(hObject.toString());
			ioRequest.setData(rwList);
			SessionSender.getInstance().send(ioRequest);
		}
	}
}
