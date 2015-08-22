package com.lezo.iscript.common.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;

public class LevelQueue<T> {
	private ConcurrentHashMap<Integer, Queue<T>> levelQueueMap = new ConcurrentHashMap<Integer, Queue<T>>();
	private List<Integer> levelDescList = new ArrayList<Integer>();
	private final int capacity;
	private final QueueFactory<T> queueFactory;

	public LevelQueue(int capacity, QueueFactory<T> queueFactory) {
		super();
		this.capacity = capacity;
		this.queueFactory = queueFactory;
	}

	public LevelQueue(int capacity) {
		this(capacity, new ArrayBlockingQueueFactory<T>());
	}

	public int offer(int level, T element) {
		int offerCount = 0;
		Queue<T> hasQueue = getOrCreateQueue(level);
		if (hasQueue.offer(element)) {
			offerCount++;
		}
		return offerCount;
	}

	public int offer(int level, List<T> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return 0;
		}
		int offerCount = 0;
		Queue<T> hasQueue = getOrCreateQueue(level);
		for (T data : dataList) {
			if (hasQueue.offer(data)) {
				offerCount++;
			} else {
				break;
			}
		}
		return offerCount;
	}

	public int size() {
		int total = 0;
		for (Entry<Integer, Queue<T>> entry : levelQueueMap.entrySet()) {
			total += entry.getValue().size();
		}
		return total;
	}

	public int size(int level) {
		Queue<T> hasQueue = levelQueueMap.get(level);
		return hasQueue == null ? 0 : hasQueue.size();
	}

	public List<T> poll(int limit) {
		List<T> destList = new ArrayList<T>(limit);
		for (Integer topLevel : levelDescList) {
			Queue<T> curQueue = getOrCreateQueue(topLevel);
			while (!curQueue.isEmpty() && limit-- > 0) {
				T element = curQueue.poll();
				destList.add(element);
			}
		}
		return destList;
	}

	public List<T> poll(int level, int limit) {
		List<T> destList = new ArrayList<T>(limit);
		Queue<T> curQueue = getOrCreateQueue(level);
		while (!curQueue.isEmpty() && limit-- > 0) {
			T element = curQueue.poll();
			destList.add(element);
		}
		return destList;
	}

	private Queue<T> getOrCreateQueue(int level) {
		Queue<T> hasQueue = levelQueueMap.get(level);
		if (hasQueue == null) {
			synchronized (levelQueueMap) {
				hasQueue = levelQueueMap.get(level);
				if (hasQueue == null) {
					hasQueue = getQueueFactory().newQueue(getCapacity());
					levelQueueMap.put(level, hasQueue);
					// add new level and sort leveList desc
					levelDescList.add(level);
					Collections.sort(levelDescList, new Comparator<Integer>() {
						@Override
						public int compare(Integer o1, Integer o2) {
							return o2.compareTo(o1);
						}
					});
				}
			}
		}
		return hasQueue;
	}

	public int getCapacity() {
		return capacity;
	}

	public QueueFactory<T> getQueueFactory() {
		return queueFactory;
	}

}
