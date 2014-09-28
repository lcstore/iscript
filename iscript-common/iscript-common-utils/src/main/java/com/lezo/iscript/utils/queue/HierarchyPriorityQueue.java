package com.lezo.iscript.utils.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class HierarchyPriorityQueue<E> {
	private final ConcurrentHashMap<String, HierarchyQueue<E>> hQueueMap = new ConcurrentHashMap<String, HierarchyQueue<E>>();
	private final Queue<HierarchyQueue<E>> queue = new PriorityQueue<HierarchyQueue<E>>();

	public void offer(List<E> elements, String type) {
		HierarchyQueue<E> hQueue = get(type);
		for (E e : elements) {
			hQueue.offer(e);
		}
	}

	public List<E> poll(int limit) {
		List<E> dataList = new ArrayList<E>();
		HierarchyQueue<E> head = queue.peek();
		System.out.println(head);
		if (head == null) {
			return dataList;
		}
		int cur = 0;
		while (cur < limit) {
			E elment = head.poll();
			if (elment == null) {
				order();
				break;
			}
			dataList.add(elment);
			cur++;
		}
		return dataList;
	}

	public void add(HierarchyQueue<E> hQueue) {
		String key = (null == hQueue.getName() ? "" : hQueue.getName())
				+ hQueue.getLevel();
		hQueueMap.put(key, hQueue);
		queue.offer(hQueue);
	}

	private HierarchyQueue<E> get(String type) {
		return hQueueMap.get(type);
	}

	// XXX 性能问题
	private void order() {
		if (queue.isEmpty()) {
			return;
		}
		HierarchyQueue<E> head = queue.poll();
		queue.offer(head);
	}

}
