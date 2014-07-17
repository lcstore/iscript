package com.lezo.iscript.common.storage;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

public class StorageBufferFactory {
	private static final Object WRITE_LOCK = new Object();
	private static final ConcurrentHashMap<String, StorageBuffer<?>> storageMap = new ConcurrentHashMap<String, StorageBuffer<?>>();
	private static final int capacity = 200;

	public static StorageBuffer<?> getStorageBuffer(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("the name for storage buffer must not be empty...");
		}
		StorageBuffer<?> hasStorageBuffer = storageMap.get(name);
		if (hasStorageBuffer == null) {
			synchronized (WRITE_LOCK) {
				hasStorageBuffer = storageMap.get(name);
				if (hasStorageBuffer == null) {
					hasStorageBuffer = new StorageBuffer<Object>(capacity);
					storageMap.put(name, hasStorageBuffer);
				}
			}
		}
		return hasStorageBuffer;
	}
}
