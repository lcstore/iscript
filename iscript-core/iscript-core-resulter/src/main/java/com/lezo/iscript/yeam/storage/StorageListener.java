package com.lezo.iscript.yeam.storage;

import java.util.Collection;

public interface StorageListener<E> {
	public void add(E data);

	public void addAll(Collection<E> dataCollection);

	public void doStorage();
}
