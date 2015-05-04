package com.lezo.iscript.yeam.resultmgr;

import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.resultmgr.listener.ConsumeListener;
import com.lezo.iscript.yeam.resultmgr.listener.ConsumeListenerManager;

public class DataLineConsumer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(DataLineConsumer.class);
	private String type;
	private String data;

	public DataLineConsumer(String type, String data) {
		super();
		this.type = type;
		this.data = data;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		Iterator<Entry<String, ConsumeListener>> it = ConsumeListenerManager.getInstance().iterator();
		while (it.hasNext()) {
			Entry<String, ConsumeListener> entry = it.next();
			entry.getValue().doConsume(type, data);
		}
		long cost = System.currentTimeMillis() - start;
		logger.info("consume type:" + type + ",data hash:" + data.hashCode() + ",cost:" + cost);
	}

}
