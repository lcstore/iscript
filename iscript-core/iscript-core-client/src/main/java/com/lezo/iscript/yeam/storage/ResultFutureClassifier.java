package com.lezo.iscript.yeam.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.mina.SessionSender;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultFutureClassifier implements Runnable {
	private Logger logger = LoggerFactory.getLogger(ResultFutureClassifier.class);
	private List<Future<ResultWritable>> copyList;

	public ResultFutureClassifier(List<Future<ResultWritable>> copyList) {
		super();
		this.copyList = copyList;
	}

	@Override
	public void run() {
		List<ResultWritable> destList = new ArrayList<ResultWritable>();
		List<Future<ResultWritable>> waitList = addDone(copyList, destList);
		addWait2Done(waitList, destList);
		sendRequest(destList);
		logger.info(String.format("Classifier done:%s,future:%s...", destList.size(), copyList.size() - destList.size()));
	}

	private void sendRequest(List<ResultWritable> rwList) {
		BatchIterator<ResultWritable> it = new BatchIterator<ResultWritable>(rwList, 20);
		while (it.hasNext()) {
			List<ResultWritable> blockList = new ArrayList<ResultWritable>(it.next());
			JSONObject hObject = HeaderUtils.getHeader();
			IoRequest ioRequest = new IoRequest();
			ioRequest.setType(IoConstant.EVENT_TYPE_RESULT);
			ioRequest.setHeader(hObject.toString());
			ioRequest.setData(blockList);
			SessionSender.getInstance().send(ioRequest);
		}
	}

	private void addWait2Done(List<Future<ResultWritable>> waitList, List<ResultWritable> rWritables) {
		if (waitList.isEmpty()) {
			return;
		}
		List<ResultWritable> rwList = rWritables;
		for (Future<ResultWritable> rsf : waitList) {
			try {
				ResultWritable rw = rsf.get();
				rwList.add(rw);
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.warn(ExceptionUtils.getStackTrace(e));
			} catch (ExecutionException e) {
				e.printStackTrace();
				logger.warn(ExceptionUtils.getStackTrace(e));
			}
		}

	}

	private List<Future<ResultWritable>> addDone(List<Future<ResultWritable>> copyList, List<ResultWritable> rWritables) {
		List<Future<ResultWritable>> waitList = new ArrayList<Future<ResultWritable>>(copyList.size());
		List<ResultWritable> rwList = rWritables;
		for (Future<ResultWritable> rsf : copyList) {
			if (rsf.isDone()) {
				try {
					ResultWritable rw = rsf.get();
					rwList.add(rw);
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.warn(ExceptionUtils.getStackTrace(e));
				} catch (ExecutionException e) {
					logger.warn(ExceptionUtils.getStackTrace(e));
					e.printStackTrace();
				}
			} else {
				waitList.add(rsf);
			}
		}
		return waitList;
	}

}
