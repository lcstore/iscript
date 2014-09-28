package com.lezo.iscript.common.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class StorageBuffer<E> {
	private BlockingQueue<E> dataQueue;
	private int capacity;

	public StorageBuffer(int capacity) {
		super();
		this.capacity = capacity;
		this.dataQueue = new ArrayBlockingQueue<E>(capacity);
	}

	public boolean add(E data) {
		return dataQueue.offer(data);
	}

	public boolean addAll(Collection<E> dataCollection) {
		for (E e : dataCollection) {
			if (!dataQueue.offer(e)) {
				return false;
			}
		}
		return true;
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
