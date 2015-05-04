package com.lezo.iscript.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class BlockingQueueTest {

	@Test
	public void testPollWhenPut() throws Exception {
		final AtomicInteger source = new AtomicInteger();
		final BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(1000);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						long start = System.currentTimeMillis();
						int value = source.incrementAndGet();
						System.out.println("put.start to put:" + value);
						blockingQueue.put(value);
						long cost = System.currentTimeMillis() - start;
						System.out.println("put.done to put:" + value + ",cost:" + cost + ",size:"
								+ blockingQueue.size());
						TimeUnit.MILLISECONDS.sleep(1);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, "put").start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (true) {
						long start = System.currentTimeMillis();
						int value = source.incrementAndGet();
						System.out.println("put2.start to put:" + value);
						blockingQueue.put(value);
						long cost = System.currentTimeMillis() - start;
						System.out.println("put2.done to put:" + value + ",cost:" + cost + ",size:"
								+ blockingQueue.size());
						TimeUnit.MILLISECONDS.sleep(1);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, "put").start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						long start = System.currentTimeMillis();
						Integer pollValue = blockingQueue.poll();
						long cost = System.currentTimeMillis() - start;
						System.out.println("poll.value:" + pollValue + ",cost:" + cost + ",size:"
								+ blockingQueue.size());
						TimeUnit.MILLISECONDS.sleep(1000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, "pool").start();
		Thread.currentThread().join();
	}
}
