package com.lezo.iscript.common.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 互斥管道队列,生产者和消费者都有互斥的独立管道进行各项作业
 * 
 * @author lezo
 * @param <E>
 * 
 */
public class MutexDuctQueue<E> extends ArrayBlockingQueue<E> {
	private static final long serialVersionUID = -3409024293307980025L;
	private final BlockingQueue<E> slaveQueue;
	private BlockingQueue<E> offer = this;
	private BlockingQueue<E> poller = this;
	private final int capacity;

	public MutexDuctQueue(int capacity) {
		super(capacity);
		this.capacity = capacity;
		this.slaveQueue = new ArrayBlockingQueue<E>(capacity);
	}

	@Override
	public boolean offer(E e) {
		swapOffer();
		return getOffer().offer(e);
	}

	@Override
	public E poll() {
		swapPoller();
		return getPoller().poll();
	}

	private void swapPoller() {
		if (poller.isEmpty()) {
			return;
		}
		if (poller == this) {
			if (!slaveQueue.isEmpty()) {
				setPoller(slaveQueue);
			}
		} else {
			if (!isEmpty()) {
				setPoller(this);
			}
		}
	}

	private void swapOffer() {
		if (offer.size() < capacity) {
			return;
		}
		if (offer == this) {
			if (slaveQueue.size() < capacity) {
				setOffer(slaveQueue);
			}
		} else {
			if (size() < capacity) {
				setOffer(this);
			}
		}
	}

	private synchronized void setOffer(BlockingQueue<E> offer) {
		this.offer = offer;
	}

	private synchronized void setPoller(BlockingQueue<E> poller) {
		this.poller = poller;
	}

	private BlockingQueue<E> getOffer() {
		return offer;
	}

	private BlockingQueue<E> getPoller() {
		return poller;
	}
}
