package com.lezo.iscript.utils.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class FairPriorityQueue<E> {
	private ExecutorService exec = Executors.newSingleThreadExecutor();
	private PushHierarchyQueue pushHierarchyQueue = new PushHierarchyQueue();
	private PollHierarchyQueue pollHierarchyQueue = new PollHierarchyQueue();

	public void offer(String type, List<E> elements) {
		pushHierarchyQueue.offer(type, elements);
	}

	public void putQueue(HierarchyQueue<E> hQueue) {
		pushHierarchyQueue.putQueue(hQueue);
	}

	public HierarchyQueue<E> getQueue(String key) {
		return pushHierarchyQueue.getQueue(key);
	}

	public List<E> poll(int limit) {
		// exec.execute(new PushQueueMover());
		new PushQueueMover().doMove();
		List<E> dataList = pollHierarchyQueue.poll(limit);
		return dataList;
	}

	class PushHierarchyQueue {
		private final ConcurrentHashMap<String, HierarchyQueue<E>> hQueueMap = new ConcurrentHashMap<String, HierarchyQueue<E>>();

		public void offer(String type, List<E> elements) {
			HierarchyQueue<E> hQueue = getQueue(type);
			if (null == hQueue) {
				throw new RuntimeException("Not Found Queue[" + type + "]");
			}
			for (E e : elements) {
				hQueue.offer(e);
			}
		}

		public void putQueue(HierarchyQueue<E> hQueue) {
			hQueueMap.put(hQueue.getKey(), hQueue);
		}

		public HierarchyQueue<E> getQueue(String key) {
			return hQueueMap.get(key);
		}

		public Set<Entry<String, HierarchyQueue<E>>> getEntrySet() {
			return Collections.unmodifiableSet(hQueueMap.entrySet());
		}

		public HierarchyQueue<E> move(String key) throws Exception {
			HierarchyQueue<E> hQueue = getQueue(key);
			if (hQueue == null) {
				return null;
			}
			return hQueue.move();
		}

		public int size() {
			return hQueueMap.size();
		}

	}

	class PollHierarchyQueue {
		private final Queue<HierarchyQueue<E>> queue = new LinkedBlockingQueue<HierarchyQueue<E>>();

		public List<E> poll(int limit) {
			List<E> dataList = new ArrayList<E>(limit);
			if (0 == size()) {
				return dataList;
			}
			HierarchyQueue<E> head = queue.peek();
			if (head == null) {
				return dataList;
			}
			int cur = 0;
			while (cur < limit) {
				E elment = head.poll();
				if (elment == null) {
					queue.poll();
					break;
				}
				dataList.add(elment);
				cur++;
			}
			return dataList;
		}

		public void offer(List<HierarchyQueue<E>> hQueueList) {
			for (HierarchyQueue<E> hQ : hQueueList) {
				queue.offer(hQ);
			}
		}

		public int size() {
			return queue.size();
		}

	}

	class PushQueueMover implements Runnable {
		public void run() {
			doMove();
		}

		public void doMove() {
			if (0 != pollHierarchyQueue.size()) {
				return;
			}
			List<HierarchyQueue<E>> orderList = new ArrayList<HierarchyQueue<E>>();
			for (Entry<String, HierarchyQueue<E>> entry : pushHierarchyQueue
					.getEntrySet()) {
				try {
					HierarchyQueue<E> queue = entry.getValue().move();
					orderList.add(queue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Collections.sort(orderList);
			pollHierarchyQueue.offer(orderList);
		}

	}

}
