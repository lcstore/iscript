package com.lezo.iscript.utils.queue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;

public class FairPriorityHierarchyQueueTest {
	private FairPriorityQueue<Object> fQueue = new FairPriorityQueue<Object>();

	@Test
	public void testOffer() {
		HierarchyQueue<Object> hQueue = new HierarchyQueue<Object>("object", 1,
				new ArrayBlockingQueue<Object>(100));
		hQueue.offer(100);
		fQueue.putQueue(hQueue);
		HierarchyQueue<Object> queue = fQueue.getQueue(hQueue.getKey());
		assertEquals(1, queue.size());
	}

	@Test
	public void testPoll() {
		HierarchyQueue<Object> hQueue = new HierarchyQueue<Object>("object", 1,
				new ArrayBlockingQueue<Object>(100));
		hQueue.offer(100);
		fQueue.putQueue(hQueue);
		List<Object> dataList = fQueue.poll(5);
		assertEquals(1, dataList.size());
	}

	@Test
	public void testPriority() {
		HierarchyQueue<Object> hQueue = new HierarchyQueue<Object>("object", 1,
				new ArrayBlockingQueue<Object>(100));
		hQueue.offer(100);
		fQueue.putQueue(hQueue);
		hQueue = new HierarchyQueue<Object>("object", 2,
				new ArrayBlockingQueue<Object>(100));
		hQueue.offer(200);
		hQueue.offer(210);
		fQueue.putQueue(hQueue);
		List<Object> dataList = fQueue.poll(5);
		assertEquals(1, dataList.size());
	}

	@Test
	public void testHLevelEmpty() {
		HierarchyQueue<Object> hQueue = new HierarchyQueue<Object>("object", 1,
				new ArrayBlockingQueue<Object>(100));
		fQueue.putQueue(hQueue);
		hQueue = new HierarchyQueue<Object>("object", 2,
				new ArrayBlockingQueue<Object>(100));
		hQueue.offer(200);
		hQueue.offer(210);
		fQueue.putQueue(hQueue);
		List<Object> dataList = fQueue.poll(5);
		assertEquals(2, dataList.size());
		List<Object> elements = new ArrayList<Object>();
		elements.add(1);
		fQueue.offer("1:object", elements);
		dataList = fQueue.poll(5);
		assertEquals(0, dataList.size());
		dataList = fQueue.poll(5);
		assertEquals(1, dataList.get(0));
	}
}
