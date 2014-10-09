package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.ObjectWriter;

public class FlushWriterTimer {
	private static Logger logger = LoggerFactory.getLogger(FlushWriterTimer.class);

	public void run() {
		long start = System.currentTimeMillis();
		Iterator<Entry<String, ObjectWriter<?>>> it = BufferWriterManager.getInstance().iterator();
		int size = 0;
		logger.info("start to flush writer...");
		while (it.hasNext()) {
			Entry<String, ObjectWriter<?>> entry = it.next();
			entry.getValue().flush();
			logger.info("flush writer:{}", entry.getKey());
			size++;
		}
		long cost = System.currentTimeMillis() - start;
		logger.info("finish to flush writer:{},cost:{}", size, cost);

	}
}
