package com.lezo.iscript.utils.queue;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class RankLinkedQueueTest {
	@Test
	public void testSort() {
		SortedSet<Integer> rankSet = Collections.synchronizedSortedSet(new TreeSet<Integer>());
		rankSet.add(1);
		rankSet.add(3);
		rankSet.add(-2);
		Iterator<Integer> it = rankSet.iterator();
		Assert.assertEquals(-2, it.next().intValue());
		Assert.assertEquals(1, it.next().intValue());
		Assert.assertEquals(3, it.next().intValue());
	}

	@Test
	public void testRank() {
		PriorityLinkedQueue<LevelObject> levelQueue = new PriorityLinkedQueue<LevelObject>();
		levelQueue.offer(new LevelObject(1, "a"));
		levelQueue.offer(new LevelObject(2, "a"));
		levelQueue.offer(new LevelObject(2, "b"));
		levelQueue.offer(new LevelObject(1, "c"));

		List<LevelObject> eList = levelQueue.poll(3);
		Assert.assertEquals(1, eList.get(0).getLevel());
		Assert.assertEquals(2, eList.get(2).getLevel());
		Assert.assertEquals("a", eList.get(2).getName());
	}

	@Test
	public void testRankList() {
		PriorityLinkedQueue<LevelObject> levelQueue = new PriorityLinkedQueue<LevelObject>();
		levelQueue.offer(new LevelObject(1, "a"));
		levelQueue.offer(new LevelObject(2, "a"));
		levelQueue.offer(new LevelObject(2, "b"));
		levelQueue.offer(new LevelObject(2, "c"));
		levelQueue.offer(new LevelObject(1, "c"));

		int limit = 3;
		List<LevelObject> eList = levelQueue.poll(limit);
		Assert.assertEquals(1, eList.get(0).getLevel());
		Assert.assertEquals(1, eList.get(1).getLevel());
		Assert.assertEquals(2, eList.get(2).getLevel());
		Assert.assertEquals("a", eList.get(2).getName());
	}

	@Test
	public void testPollSize() {
		PriorityLinkedQueue<LevelObject> levelQueue = new PriorityLinkedQueue<LevelObject>();
		levelQueue.offer(new LevelObject(1, "a"));
		levelQueue.offer(new LevelObject(2, "a"));
		levelQueue.offer(new LevelObject(2, "b"));
		levelQueue.offer(new LevelObject(2, "c"));
		levelQueue.offer(new LevelObject(1, "c"));

		int limit = 2;
		List<LevelObject> eList = levelQueue.poll(limit);
		Assert.assertEquals(limit, eList.size());
	}

	@Test
	public void testThread() {
		final PriorityLinkedQueue<LevelObject> levelQueue = new PriorityLinkedQueue<LevelObject>();

		Thread producer = new Thread(new Runnable() {
			public void run() {
				while (true) {
					Random p = new Random();
					Random name = new Random();
					Random level = new Random();
					int l = level.nextInt(5);
					int iName = name.nextInt(100);
					LevelObject e = new LevelObject(l, iName + "");
					levelQueue.offer(e);
					System.out.println("producer:" + e.getLevel() + ":" + e.getName());
					if (p.nextBoolean()) {
						try {
							TimeUnit.MILLISECONDS.sleep(1000);
						} catch (InterruptedException ee) {
							ee.printStackTrace();
						}
					}
				}
			}
		});
		Thread consumer = new Thread(new Runnable() {
			public void run() {
				while (true) {
					Random level = new Random();
					int limit = level.nextInt(10) + 1;
					List<LevelObject> eList = levelQueue.poll(limit);
					for (LevelObject e : eList) {
						System.out.println("consumer:" + e.getLevel() + ":" + e.getName());
					}
					if (level.nextBoolean()) {
						try {
							TimeUnit.MILLISECONDS.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		consumer.start();
		producer.start();
		try {
			Thread.currentThread().sleep(60 * 1000);
			System.exit(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
