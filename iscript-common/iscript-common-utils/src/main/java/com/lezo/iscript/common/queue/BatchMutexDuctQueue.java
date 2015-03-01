package com.lezo.iscript.common.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 互斥管道队列,生产者和消费者都有互斥的独立管道进行各项作业
 * 
 * @author lezo
 * @param <E>
 * 
 */
public class BatchMutexDuctQueue<E> extends ArrayBlockingQueue<E> {
	private static final long serialVersionUID = -3409024293307980025L;
	private final AtomicLong lastStamp = new AtomicLong(System.currentTimeMillis());
	private final BlockingQueue<List<E>> cosumeQueue;
	private final int bufferCapacity;
	private final long forceCosumeTime;

	public BatchMutexDuctQueue(int capacity, int cosumeCapacity, long forceCosumeTime) {
		// buffer quue
		super(capacity);
		this.bufferCapacity = capacity;
		this.forceCosumeTime = forceCosumeTime;
		this.cosumeQueue = new ArrayBlockingQueue<List<E>>(cosumeCapacity);
	}

	@Override
	public boolean offer(E e) {
		boolean bStatus = super.offer(e);
		if (isConsume()) {
			List<E> eList = new ArrayList<E>(super.size());
			if (super.drainTo(eList) > 0) {
				this.cosumeQueue.offer(eList);
			}
		}
		return bStatus;
	}

	public List<E> pollMore() {
		List<E> eList= this.cosumeQueue.poll();
		if(eList ==null && isForceCosume()){
			eList = new ArrayList<E>(super.size());
			if (super.drainTo(eList) > 0) {
				this.cosumeQueue.offer(eList);
			}
		}
		return eList;
	}

	private boolean isForceCosume() {
		if (super.isEmpty()) {
			return false;
		}
		boolean isTimeOut = System.currentTimeMillis() - lastStamp.get() > this.forceCosumeTime;
		return isTimeOut;
	}
	private boolean isConsume() {
		return super.size() >= this.bufferCapacity;
	}
}
