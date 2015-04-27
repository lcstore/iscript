package com.lezo.iscript.yeam.storage;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.resultmgr.DataMessageHandler;

public class ProxyDetectSummary {
	private static Logger logger = LoggerFactory.getLogger(DataMessageHandler.class);
	private static AtomicBoolean running = new AtomicBoolean(false);

	public void run() {
		if (running.get()) {
			logger.warn(this.getClass().getSimpleName() + " is running..");
			return;
		}
		long start = System.currentTimeMillis();
		try {
			logger.info("start to do DataMessageHandler ..");
			running.set(true);
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("fail to summary proxy detect.cost:%s.cause:", cost);
			logger.warn(msg, e);
		} finally {
			running.set(false);
		}
	}
}
