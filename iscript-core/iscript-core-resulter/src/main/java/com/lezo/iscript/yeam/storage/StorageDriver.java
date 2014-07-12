package com.lezo.iscript.yeam.storage;

import java.util.concurrent.ConcurrentHashMap;

public class StorageDriver {
	private ConcurrentHashMap<Class<?>, StorageListener<?>> listenerMap = new ConcurrentHashMap<Class<?>, StorageListener<?>>();

	@SuppressWarnings("unchecked")
	public <T> StorageListener<T> getListener(Class<T> dataClass) {
		StorageListener<?> listener = listenerMap.get(dataClass);
		if (listener == null) {
			return null;
		}
		return (StorageListener<T>) listener;
	}

	public void addListener(Class<?> dataClass, StorageListener<?> listener) {
		listenerMap.putIfAbsent(dataClass, listener);
	}
}
