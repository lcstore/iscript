package com.lezo.iscript.yeam.storage;

public interface StorageTrigger {

	public void doTrigger();

	void addListener(Class<?> dataClass, StorageListener<?> listener);
}
