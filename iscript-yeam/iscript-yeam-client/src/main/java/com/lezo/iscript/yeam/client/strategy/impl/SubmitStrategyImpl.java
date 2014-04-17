package com.lezo.iscript.yeam.client.strategy.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.client.result.ResultsCaller;
import com.lezo.iscript.yeam.client.result.ResultsHolder;
import com.lezo.iscript.yeam.client.result.SubmitCallable;
import com.lezo.iscript.yeam.client.result.SubmitFuture;
import com.lezo.iscript.yeam.client.result.SubmitsHolder;
import com.lezo.iscript.yeam.strategy.Strategyable;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class SubmitStrategyImpl implements Strategyable {
	private static Logger log = Logger.getLogger(SubmitStrategyImpl.class);

	@Override
	public void doStrategy() throws Exception {
		BlockingQueue<SubmitFuture> submitQueue = SubmitsHolder.getInstance().getSubmitQueue();
		List<Future<ResultWritable>> doneList = new ArrayList<Future<ResultWritable>>();
		List<Future<ResultWritable>> cancleList = new ArrayList<Future<ResultWritable>>();
		sortResultFuture(doneList, cancleList);
		submitResults(doneList, submitQueue);
		clearEndFuture(doneList, cancleList);
		clearEndSubmit(submitQueue);
		long timeout = ClientConstant.SUBMIT_INTERVAL_TIME;
		sleep(timeout);
	}

	private void sleep(long timeout) throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(timeout);
	}

	private void clearEndSubmit(BlockingQueue<SubmitFuture> submitQueue) {
		if (submitQueue.isEmpty()) {
			return;
		}
		Iterator<SubmitFuture> it = submitQueue.iterator();
		List<SubmitFuture> doneList = new ArrayList<SubmitFuture>();
		List<SubmitFuture> cancleList = new ArrayList<SubmitFuture>();
		Date toDate = new Date();
		// get done or cancle future
		while (it.hasNext()) {
			SubmitFuture sfo = it.next();
			if (sfo.getFuture().isDone()) {
				sfo.setToDate(toDate);
				doneList.add(sfo);
			} else if (sfo.getFuture().isCancelled()) {
				sfo.setToDate(toDate);
				cancleList.add(sfo);
			}
		}
		// remove finish future
		for (SubmitFuture done : doneList) {
			submitQueue.remove(done);
		}
		for (SubmitFuture cancle : cancleList) {
			submitQueue.remove(cancle);
		}

	}

	private void clearEndFuture(List<Future<ResultWritable>> doneList, List<Future<ResultWritable>> cancleList) {
		BlockingQueue<Future<ResultWritable>> resultQueue = ResultsHolder.getInstance().getResultQueue();
		for (Future<ResultWritable> done : doneList) {
			resultQueue.remove(done);
		}
		for (Future<ResultWritable> cancle : cancleList) {
			resultQueue.remove(cancle);
		}
	}

	/**
	 * 执行的任务分类为完成、取消、执行中
	 * 
	 * @param doneList
	 * @param cancleList
	 */
	private void sortResultFuture(List<Future<ResultWritable>> doneList, List<Future<ResultWritable>> cancleList) {
		BlockingQueue<Future<ResultWritable>> resultQueue = ResultsHolder.getInstance().getResultQueue();
		Iterator<Future<ResultWritable>> it = resultQueue.iterator();
		while (it.hasNext()) {
			Future<ResultWritable> resultFuture = it.next();
			if (resultFuture.isDone()) {
				doneList.add(resultFuture);
			} else if (resultFuture.isCancelled()) {
				cancleList.add(resultFuture);
			}
		}
	}

	/**
	 * 异步提交结果
	 * 
	 * @param doneList
	 * @param submitQueue
	 */
	private void submitResults(List<Future<ResultWritable>> doneList, BlockingQueue<SubmitFuture> submitQueue) {
		List<ResultWritable> resultList = new ArrayList<ResultWritable>(doneList.size());
		ThreadPoolExecutor caller = ResultsCaller.getInstance().getCaller();
		Map<String, List<ResultWritable>> resulterMap = new HashMap<String, List<ResultWritable>>();
		for (Future<ResultWritable> done : doneList) {
			try {
				ResultWritable result = done.get();
				if (result != null) {
					Object rsHostObj = result.getTask().get(ClientConstant.CLIENT_RESULTER_HOST);
					String rsHost = "";
					if (rsHostObj != null) {
						rsHost = (String) rsHostObj;
					}
					List<ResultWritable> rsList = resulterMap.get(rsHost);
					if (rsList == null) {
						rsList = new ArrayList<ResultWritable>();
						resulterMap.put(rsHost, rsList);
					}
					rsList.add(result);
				}
			} catch (InterruptedException e) {
				log.warn("", e);
			} catch (ExecutionException e) {
				log.warn("", e);
			}
		}
		if (!resulterMap.isEmpty()) {
			for (Entry<String, List<ResultWritable>> entry : resulterMap.entrySet()) {
				String rsHost = entry.getKey();
				List<ResultWritable> rsList = entry.getValue();
				Future<List<Long>> submitFuture = caller.submit(new SubmitCallable(rsHost, rsList));
				SubmitFuture sfo = new SubmitFuture();
				sfo.setFromDate(new Date());
				sfo.setFuture(submitFuture);
				sfo.setSubmitSize(resultList.size());
				submitQueue.add(sfo);
			}
		}
		log.info("submit.thread[active:" + caller.getActiveCount() + ",largest:" + caller.getLargestPoolSize()
				+ ",max:" + caller.getMaximumPoolSize() + "],task[add:" + resultList.size() + ",work:"
				+ caller.getQueue().size() + ",sum:" + caller.getCompletedTaskCount() + "]");

	}
}
