package com.lezo.iscript.utils.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

public class MapBlockingQueue<E> {
	private Logger log = Logger.getLogger(MapBlockingQueue.class);
	private final ConcurrentHashMap<String, BlockingQueue<E>> mapQueue = new ConcurrentHashMap<String, BlockingQueue<E>>();
	private final Semaphore blockMark = new Semaphore(1, true);
	private final Set<String> keySet = Collections.synchronizedSet(new LinkedHashSet<String>());
	private final Set<String> emptySet = Collections.synchronizedSet(new HashSet<String>());
	private volatile int queueSize = 0;

	public MapBlockingQueue() {
		try {
			blockMark.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addQueue(String type, BlockingQueue<E> queue) {
		if (queue.isEmpty()) {
			this.emptySet.add(type);
		}
		this.keySet.add(type);
		this.mapQueue.put(type, queue);
		this.queueSize++;
	}

	public boolean contains(String type) {
		return this.keySet.contains(type);
	}

	public void offer(String type, final List<E> tasks) {
		if (tasks.isEmpty()) {
			return;
		}
		if (!mapQueue.containsKey(type)) {
			return;
		}
		BlockingQueue<E> activeQueue = mapQueue.get(type);
		for (E task : tasks) {
			activeQueue.offer(task);
		}
		release(type);
	}

	public void put(String type, final List<E> tasks) {
		if (tasks.isEmpty()) {
			return;
		}
		if (!mapQueue.containsKey(type)) {
			return;
		}
		BlockingQueue<E> activeQueue = mapQueue.get(type);
		for (E task : tasks) {
			try {
				activeQueue.put(task);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		release(type);
	}

	public List<E> poll(String type, int max) {
		List<E> taskList = new ArrayList<E>(max);
		if (!mapQueue.containsKey(type)) {
			return taskList;
		}
		BlockingQueue<E> activeQueue = mapQueue.get(type);
		int size = max;
		while (size > 0) {
			E task = activeQueue.poll();
			if (task == null) {
				emptySet.add(type);
				break;
			}
			taskList.add(task);
		}
		return taskList;
	}

	public List<E> take(String type, int max) {
		List<E> taskList = new ArrayList<E>(max);
		if (!mapQueue.containsKey(type)) {
			return taskList;
		}
		BlockingQueue<E> activeQueue = mapQueue.get(type);
		int size = max;
		while (size > 0) {
			E task = null;
			try {
				task = activeQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error("when take type[" + type + "],cause exception:", e);
			}
			if (task == null) {
				emptySet.add(type);
				break;
			}
			taskList.add(task);
		}
		return taskList;
	}

	private void release(String type) {
		blockMark.release();
		emptySet.remove(type);
	}

	public void doBlock() throws InterruptedException {
		if (isBlocking()) {
			blockMark.acquire();
			log.info("<Block>,the queue is empty..");
		}
	}

	private boolean isBlocking() throws InterruptedException {
		return (emptySet.size() == queueSize);
	}

	public void setLogger(Logger log) {
		this.log = log;
	}

	public Set<String> getKeySet() {
		return keySet;
	}
}
