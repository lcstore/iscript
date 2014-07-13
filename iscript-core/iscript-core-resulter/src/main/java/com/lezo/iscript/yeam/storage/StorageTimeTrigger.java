package com.lezo.iscript.yeam.storage;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

public class StorageTimeTrigger implements StorageTrigger {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(StorageTimeTrigger.class);
	private ConcurrentHashMap<Class<?>, StorageListener<?>> listenerMap = new ConcurrentHashMap<Class<?>, StorageListener<?>>();

	@Override
	public void addListener(Class<?> dataClass, StorageListener<?> listener) {
		listenerMap.putIfAbsent(dataClass, listener);
	}

	@Override
	public void doTrigger() {
		logger.info("start to trigger listener:" + listenerMap.size());
		long start = System.currentTimeMillis();
		for (Entry<Class<?>, StorageListener<?>> entry : listenerMap.entrySet()) {
			if (logger.isDebugEnabled()) {
				logger.debug("trigger listener:" + entry.getKey().getName());
			}
			entry.getValue().doStorage();
		}
		long cost = System.currentTimeMillis() - start;
		logger.info("finish to trigger listener:" + listenerMap.size() + ",cost:" + cost);
	}

	public void setListeners(List<StorageListener<?>> listeners) {
		if (listeners == null) {
			return;
		}
		for (StorageListener<?> storageListener : listeners) {
			addListener(storageListener.getClass(), storageListener);
		}
	}
}
