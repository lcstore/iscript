package com.lezo.iscript.yeam.client.strategy.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.client.result.ResultsCaller;
import com.lezo.iscript.yeam.client.result.ResultsHolder;
import com.lezo.iscript.yeam.client.result.SubmitCallable;
import com.lezo.iscript.yeam.strategy.Strategyable;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class SubmitStrategyImpl implements Strategyable {
	private static Logger log = Logger.getLogger(SubmitStrategyImpl.class);

	@Override
	public void doStrategy() throws Exception {
		Iterator<Entry<String, List<ResultWritable>>> it = ResultsHolder.getInstance().iterator();
		ResultsHolder.getInstance().clear();
		ThreadPoolExecutor caller = ResultsCaller.getInstance().getCaller();
		List<Future<List<Long>>> resultSubmits = new ArrayList<Future<List<Long>>>();
		int total = 0;
		while (it.hasNext()) {
			Entry<String, List<ResultWritable>> entry = it.next();
			String rsHost = entry.getKey();
			List<ResultWritable> rsList = entry.getValue();
			total += rsList.size();
			Future<List<Long>> future = caller.submit(new SubmitCallable(rsHost, rsList));
			resultSubmits.add(future);
		}
		for (Future<List<Long>> submit : resultSubmits) {
			try {
				submit.get();
			} catch (Exception e) {
				log.warn("sumit result.cause:", e);
			}
		}
		log.info("submit.thread[active:" + caller.getActiveCount() + ",largest:" + caller.getLargestPoolSize()
				+ ",max:" + caller.getMaximumPoolSize() + "],task[add:" + total + ",work:" + caller.getQueue().size()
				+ ",sum:" + caller.getCompletedTaskCount() + "]");
		long timeout = ClientConstant.SUBMIT_INTERVAL_TIME;
		sleep(timeout);
	}

	private void sleep(long timeout) throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(timeout);
	}
}
