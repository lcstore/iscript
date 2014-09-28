package com.lezo.iscript.utils.queue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Assert;
import org.junit.Test;

public class HierarchyQueueTest {


	@Test
	public void testSameSize() {
		HierarchyQueue<Object> aLevel = new HierarchyQueue<Object>("a", 1,
				new ArrayBlockingQueue<Object>(10));
		HierarchyQueue<Object> bLevel = new HierarchyQueue<Object>("b", 2,
				new ArrayBlockingQueue<Object>(10));

		Queue<HierarchyQueue<Object>> priorityQueue = new PriorityQueue<HierarchyQueue<Object>>();
		// aLevel.offer(new Object());
		// bLevel.offer(new Object());
		priorityQueue.add(bLevel);
		priorityQueue.add(aLevel);

		Assert.assertEquals(aLevel, priorityQueue.poll());
	}

	@Test
	public void testNotSameSize() {
		HierarchyQueue<Object> aLevel = new HierarchyQueue<Object>("a", 1,
				new ArrayBlockingQueue<Object>(10));
		HierarchyQueue<Object> bLevel = new HierarchyQueue<Object>("b", 2,
				new ArrayBlockingQueue<Object>(10));

		Queue<HierarchyQueue<Object>> priorityQueue = new PriorityQueue<HierarchyQueue<Object>>();
		// aLevel.offer(new Object());
		bLevel.offer(new Object());
		priorityQueue.add(bLevel);
		priorityQueue.add(aLevel);

		Assert.assertEquals(bLevel, priorityQueue.poll());
	}

	@Test
	public void testHightLevel() {
		HierarchyQueue<Object> aLevel = new HierarchyQueue<Object>("a", 1,
				new ArrayBlockingQueue<Object>(10));
		HierarchyQueue<Object> bLevel = new HierarchyQueue<Object>("b", 2,
				new ArrayBlockingQueue<Object>(10));

		Queue<HierarchyQueue<Object>> priorityQueue = new PriorityQueue<HierarchyQueue<Object>>();
		aLevel.offer(new Object());
		// bLevel.offer(new Object());
		priorityQueue.add(bLevel);
		priorityQueue.add(aLevel);

		Assert.assertEquals(aLevel, priorityQueue.poll());
	}

	@Test
	public void testChangeSize() {
		HierarchyQueue<Object> aLevel = new HierarchyQueue<Object>("a", 1,
				new ArrayBlockingQueue<Object>(10));
		HierarchyQueue<Object> bLevel = new HierarchyQueue<Object>("b", 2,
				new ArrayBlockingQueue<Object>(10));
		aLevel.offer(new Object());
		aLevel.offer(new Object());
		aLevel.offer(new Object());
		// aLevel.offer(new Object());
		// aLevel.offer(new Object());
		bLevel.offer(new Object());
		bLevel.offer(new Object());
		Queue<HierarchyQueue<Object>> priorityQueue = new PriorityQueue<HierarchyQueue<Object>>();
		priorityQueue.add(aLevel);
		priorityQueue.add(bLevel);

		HierarchyQueue<Object> top = null;
		while (priorityQueue.peek().size() > 0) {
			display(priorityQueue);
			top = priorityQueue.poll();
			top.poll();
			priorityQueue.offer(top);
		}

		display(priorityQueue);
	}

	@Test
	public void doBatch() throws InterruptedException {
		final HierarchyPriorityQueue<Object> hpq = new HierarchyPriorityQueue<Object>();
		hpq.add(new HierarchyQueue<Object>("a", 1,
				new ArrayBlockingQueue<Object>(10)));
		hpq.add(new HierarchyQueue<Object>("b", 2,
				new ArrayBlockingQueue<Object>(10)));

		List<Object> elements = new ArrayList<Object>();
		elements.add(new Object());
		elements.add(new Object());
		hpq.offer(elements, "a" + 1);
		hpq.offer(elements, "b" + 2);
		int size = 2;
		while (true) {
			List<Object> data = hpq.poll(1);
			System.out.println("----------:" + data.size());
			if (data.isEmpty()) {
				size--;
			}
			if (size < 1) {
				break;
			}
		}

	}

	private void display(Queue<HierarchyQueue<Object>> priorityQueue) {
		Iterator<HierarchyQueue<Object>> it = priorityQueue.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
		System.out.println("----------");
	}
}
