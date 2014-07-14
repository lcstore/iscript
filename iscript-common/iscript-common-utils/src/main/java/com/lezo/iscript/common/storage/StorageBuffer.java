package com.lezo.iscript.common.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class StorageBuffer<E> {
	private final Object lock = new Object();
	private List<E> dataList;
	private int capacity;

	public StorageBuffer(int capacity) {
		super();
		this.capacity = capacity;
		this.dataList = new ArrayList<E>(this.capacity);
	}

	public void add(E data) {
		if (data == null) {
			return;
		}
		synchronized (lock) {
			dataList.add(data);
		}
	}

	public void addAll(Collection<E> dataCollection) {
		if (CollectionUtils.isEmpty(dataCollection)) {
			return;
		}
		synchronized (lock) {
			for (E data : dataCollection) {
				dataList.add(data);
			}
		}
	}

	public List<E> moveTo() {
		List<E> copyList = Collections.emptyList();
		if (CollectionUtils.isEmpty(dataList)) {
			return copyList;
		}
		synchronized (lock) {
			if (!CollectionUtils.isEmpty(dataList)) {
				// copyList = (List<E>) Arrays.asList(new
				// Object[dataList.size()]);
				// Collections.copy(dataList, copyList);
				// dataList.clear();
				copyList = dataList;
				dataList = new ArrayList<E>(capacity);
			}
		}
		return copyList;
	}

	public int getCapacity() {
		return capacity;
	}

	public int size() {
		return dataList.size();
	}
}
