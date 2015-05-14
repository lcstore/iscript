package com.lezo.iscript.yeam.resultmgr.job;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummaryProxyJob {
	private static Logger logger = LoggerFactory.getLogger(SummaryProxyJob.class);
	private static AtomicBoolean running = new AtomicBoolean(false);
	
	public void run(){
		if (running.get()) {
			logger.warn("EarliestMessageHandler is running..");
			return;
		}
		long start = System.currentTimeMillis();
		try {
			running.set(true);
//			logger.info("start to do EarliestMessageHandler,name size:" + nameList.size());
			long cost = System.currentTimeMillis() - start;
//			logger.info("add earliest message:" + dirBeans.size() + ",nameCount:" + nameList.size() + ",cost:" + cost);
		} catch (Exception e) {
			logger.warn("", e);
		} finally {
			running.set(false);
		}
	}
}
