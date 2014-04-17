package com.lezo.iscript.utils.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityLinkedQueue<E extends Priorityable> {
	private final ConcurrentHashMap<Integer, LinkedBlockingQueue<E>> rankMap = new ConcurrentHashMap<Integer, LinkedBlockingQueue<E>>();
	private final SortedSet<Integer> rankSet = Collections.synchronizedSortedSet(new TreeSet<Integer>());
	private final Object lock = new Object();
	private final AtomicInteger count = new AtomicInteger(0);
	private final int capacity;

	public PriorityLinkedQueue() {
		this(16);
	}

	public PriorityLinkedQueue(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Except capacity more than 0,but<" + capacity + ">");
		}
		this.capacity = capacity;
	}

	private LinkedBlockingQueue<E> getQueue(Integer rank) {
		return rankMap.get(rank);
	}

	private void offerQueue(Integer rank, LinkedBlockingQueue<E> queue) {
		synchronized (lock) {
			rankSet.add(rank);
			rankMap.put(rank, queue);
		}
	}

	private void pollQueue() {
		if (rankSet.isEmpty()) {
			return;
		}
		Integer rank = rankSet.first();
		synchronized (lock) {
			rankMap.remove(rank);
			rankSet.remove(rank);
		}
	}

	private LinkedBlockingQueue<E> peekQueue() {
		Integer topLevel = 0;
		if (rankSet.isEmpty()) {
			return null;
		}
		topLevel = rankSet.first();
		return getQueue(topLevel);
	}

	@Deprecated
	public void offer(E e) {
		LinkedBlockingQueue<E> queue = getQueue(e.getPriority());
		if (queue == null) {
			queue = new LinkedBlockingQueue<E>(this.capacity);
			offerQueue(e.getPriority(), queue);
		}
		if (queue.offer(e)) {
			this.count.incrementAndGet();
		}
	}

	public void offer(List<E> elemets) {
		Map<Integer, List<E>> eMap = new HashMap<Integer, List<E>>();
		for (E e : elemets) {
			List<E> eList = eMap.get(e.getPriority());
			if (eList == null) {
				eList = new ArrayList<E>();
				eMap.put(e.getPriority(), eList);
			}
			eList.add(e);
		}
		for (Entry<Integer, List<E>> entry : eMap.entrySet()) {
			LinkedBlockingQueue<E> queue = getQueue(entry.getKey());
			if (queue == null) {
				queue = new LinkedBlockingQueue<E>(this.capacity);
				offerQueue(entry.getKey(), queue);
			}
			for (E ele : entry.getValue()) {
				if (queue.offer(ele)) {
					this.count.addAndGet(entry.getValue().size());
					System.err.println(this.count.get() + "," + elemets.size());
				}
			}
		}
	}

	public List<E> poll(int limit) {
		if (size() < 1 || limit < 1) {
			return new ArrayList<E>(0);
		}
		List<E> eList = new ArrayList<E>(limit);
		LinkedBlockingQueue<E> queue = null;
		while ((queue = peekQueue()) != null) {
			synchronized (queue) {
				if (queue.isEmpty()) {
					pollQueue();
					continue;
				}
				while (!queue.isEmpty()) {
					this.count.decrementAndGet();
					E e = queue.poll();
					eList.add(e);
					if (--limit == 0) {
						return eList;
					}
				}
			}
		}
		return eList;
	}

	public int size() {
		return this.count.get();
	}

	public boolean isEmpty() {
		return 0 == size();
	}

}
