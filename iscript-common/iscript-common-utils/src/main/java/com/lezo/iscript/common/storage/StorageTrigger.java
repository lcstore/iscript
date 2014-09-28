package com.lezo.iscript.common.storage;

public interface StorageTrigger {

	public void doTrigger();

	void addListener(Class<?> dataClass, StorageListener<?> listener);

	StorageListener<?> getListener(Class<?> dataClass);
}
