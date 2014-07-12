package com.lezo.iscript.yeam.storage;

import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

public class StorageTimeTrigger implements StorageTrigger {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(StorageTimeTrigger.class);
	private ConcurrentHashMap<Class<?>, StorageListener<?>> listenerMap = new ConcurrentHashMap<Class<?>, StorageListener<?>>();
	private Timer trigger;

	public StorageTimeTrigger(long delay, long period) {
		super();
		this.trigger = new Timer();
		this.trigger.schedule(new TimerTask() {
			@Override
			public void run() {
				doTrigger();

			}
		}, delay, period);
	}

	@SuppressWarnings("unchecked")
	public <T> StorageListener<T> getListener(Class<T> dataClass) {
		StorageListener<?> listener = listenerMap.get(dataClass);
		if (listener == null) {
			return null;
		}
		return (StorageListener<T>) listener;
	}

	@Override
	public void addListener(Class<?> dataClass, StorageListener<?> listener) {
		listenerMap.putIfAbsent(dataClass, listener);
	}

	@Override
	public void doTrigger() {
		for (Entry<Class<?>, StorageListener<?>> entry : listenerMap.entrySet()) {
			if (logger.isDebugEnabled()) {
				logger.debug("trigger listener:" + entry.getKey().getName());
			}
			entry.getValue().doStorage();
		}
	}
}
