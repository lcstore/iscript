package com.lezo.iscript.common.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections4.CollectionUtils;

public class StorageBuffer<E> {
	private BlockingQueue<E> dataQueue;
	private int capacity;

	public StorageBuffer(int capacity) {
		super();
		this.capacity = capacity;
		this.dataQueue = new LinkedBlockingQueue<E>(capacity);
	}

	public void add(E data) {
		if (data == null) {
			return;
		}
		dataQueue.offer(data);
	}

	public void addAll(Collection<E> dataCollection) {
		if (CollectionUtils.isEmpty(dataCollection)) {
			return;
		}
		for (E e : dataCollection) {
			dataQueue.offer(e);
		}
	}

	public List<E> moveTo() {
		List<E> copyList = Collections.emptyList();
		if (dataQueue.isEmpty()) {
			return copyList;
		}
		int size = dataQueue.size();
		copyList = new ArrayList<E>(size);
		while (size-- > 0) {
			E data = dataQueue.poll();
			if (data == null) {
				break;
			} else {
				copyList.add(data);
			}
		}
		return copyList;
	}

	public int getCapacity() {
		return capacity;
	}

	public int size() {
		return dataQueue.size();
	}
}
