package com.lezo.iscript.utils.queue;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyLinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7162614510445198903L;

	/**
	 * 链表节点node类结构
	 */
	static class Node<E> {
		volatile E item;// volatile使得所有的write happen-befor read，保证了数据的可见性
		Node<E> next;

		Node(E x) {
			item = x;
		}
	}

	/** 队列容量，默认为Integer.MAX_VALUE */
	private final int capacity;

	/** 用原子变量 表示当前元素的个数 */
	private final AtomicInteger count = new AtomicInteger(0);

	/** 表头节点 */
	private transient Node<E> head;

	/** 表尾节点 */
	private transient Node<E> last;

	/** 获取元素或删除元素时 要加的takeLock锁 */
	private final ReentrantLock takeLock = new ReentrantLock();

	/** 获取元素 notEmpty条件 */
	private final Condition notEmpty = takeLock.newCondition();

	/** 插入元素时 要加putLock锁 */
	private final ReentrantLock putLock = new ReentrantLock();

	/** 插入时，要判满 */
	private final Condition notFull = putLock.newCondition();

	/**
	 * 唤醒等待的take操作，在put/offer中调用（因为这些操作中不会用到takeLock锁）
	 */
	private void signalNotEmpty() {
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
	}

	/**
	 * 唤醒等待插入操作，在take/poll中调用.
	 */
	private void signalNotFull() {
		final ReentrantLock putLock = this.putLock;
		putLock.lock();
		try {
			notFull.signal();
		} finally {
			putLock.unlock();
		}
	}

	/**
	 * 插入到尾部
	 */
	private void insert(E x) {
		last = last.next = new Node<E>(x);
	}

	/**
	 * 获取并移除头元素
	 */
	private E extract() {
		Node<E> first = head.next;
		head = first;
		E x = first.item;
		first.item = null;
		return x;
	}

	/**
	 * 锁住两把锁，在remove，clear等方法中调用
	 */
	private void fullyLock() {
		putLock.lock();
		takeLock.lock();
	}

	/**
	 * 和fullyLock成对使用
	 */
	private void fullyUnlock() {
		takeLock.unlock();
		putLock.unlock();
	}

	/**
	 * 默认构造，容量为 Integer.MAX_VALUE
	 */
	public MyLinkedBlockingQueue() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * 指定容量的构造
	 */
	public MyLinkedBlockingQueue(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
		last = head = new Node<E>(null);
	}

	/**
	 * 指定初始化集合的构造
	 */
	public MyLinkedBlockingQueue(Collection<? extends E> c) {
		this(Integer.MAX_VALUE);
		for (E e : c)
			add(e);
	}

	/**
	 * 通过原子变量，直接获得大小
	 */
	public int size() {
		return count.get();
	}

	/**
	 * 返回理想情况下（没有内存和资源约束）此队列可接受并且不会被阻塞的附加元素数量。
	 */
	public int remainingCapacity() {
		return capacity - count.get();
	}

	/**
	 * 将指定元素插入到此队列的尾部，如有必要，则等待空间变得可用。
	 */
	public void put(E e) throws InterruptedException {
		if (e == null)
			throw new NullPointerException();
		int c = -1;
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		putLock.lockInterruptibly();
		try {
			try {
				while (count.get() == capacity)
					notFull.await();
			} catch (InterruptedException ie) {
				notFull.signal(); // propagate to a non-interrupted thread
				throw ie;
			}
			insert(e);
			c = count.getAndIncrement();
			if (c + 1 < capacity)
				notFull.signal();
		} finally {
			putLock.unlock();
		}
		if (c == 0)
			signalNotEmpty();
	}

	/**
	 * 将指定元素插入到此队列的尾部，如有必要，则等待指定的时间以使空间变得可用。
	 */
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {

		if (e == null)
			throw new NullPointerException();
		long nanos = unit.toNanos(timeout);
		int c = -1;
		final ReentrantLock putLock = this.putLock;
		final AtomicInteger count = this.count;
		putLock.lockInterruptibly();
		try {
			for (;;) {
				if (count.get() < capacity) {
					insert(e);
					c = count.getAndIncrement();
					if (c + 1 < capacity)
						notFull.signal();
					break;
				}
				if (nanos <= 0)
					return false;
				try {
					nanos = notFull.awaitNanos(nanos);
				} catch (InterruptedException ie) {
					notFull.signal(); // propagate to a non-interrupted thread
					throw ie;
				}
			}
		} finally {
			putLock.unlock();
		}
		if (c == 0)
			signalNotEmpty();
		return true;
	}

	/**
	 * 将指定元素插入到此队列的尾部（如果立即可行且不会超出此队列的容量）， 在成功时返回 true，如果此队列已满，则返回 false。
	 */
	public boolean offer(E e) {
		if (e == null)
			throw new NullPointerException();
		final AtomicInteger count = this.count;
		if (count.get() == capacity)
			return false;
		int c = -1;
		final ReentrantLock putLock = this.putLock;
		putLock.lock();
		try {
			if (count.get() < capacity) {
				insert(e);
				c = count.getAndIncrement();
				if (c + 1 < capacity)
					notFull.signal();
			}
		} finally {
			putLock.unlock();
		}
		if (c == 0)
			signalNotEmpty();
		return c >= 0;
	}

	// 获取并移除此队列的头部，在元素变得可用之前一直等待（如果有必要）。
	public E take() throws InterruptedException {
		E x;
		int c = -1;
		final AtomicInteger count = this.count;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lockInterruptibly();
		try {
			try {
				while (count.get() == 0)
					notEmpty.await();
			} catch (InterruptedException ie) {
				notEmpty.signal(); // propagate to a non-interrupted thread
				throw ie;
			}

			x = extract();
			c = count.getAndDecrement();
			if (c > 1)
				notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
		if (c == capacity)
			signalNotFull();
		return x;
	}

	// 获取并移除此队列的头部，在指定的等待时间前等待可用的元素（如果有必要
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		E x = null;
		int c = -1;
		long nanos = unit.toNanos(timeout);
		final AtomicInteger count = this.count;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lockInterruptibly();
		try {
			for (;;) {
				if (count.get() > 0) {
					x = extract();
					c = count.getAndDecrement();
					if (c > 1)
						notEmpty.signal();
					break;
				}
				if (nanos <= 0)
					return null;
				try {
					nanos = notEmpty.awaitNanos(nanos);
				} catch (InterruptedException ie) {
					notEmpty.signal(); // propagate to a non-interrupted thread
					throw ie;
				}
			}
		} finally {
			takeLock.unlock();
		}
		if (c == capacity)
			signalNotFull();
		return x;
	}

	// 获取并移除此队列的头，如果此队列为空，则返回 null。
	public E poll() {
		final AtomicInteger count = this.count;
		if (count.get() == 0)
			return null;
		E x = null;
		int c = -1;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			if (count.get() > 0) {
				x = extract();
				c = count.getAndDecrement();
				if (c > 1)
					notEmpty.signal();
			}
		} finally {
			takeLock.unlock();
		}
		if (c == capacity)
			signalNotFull();
		return x;
	}

	// 获取但不移除此队列的头；如果此队列为空，则返回 null。
	public E peek() {
		if (count.get() == 0)
			return null;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			Node<E> first = head.next;
			if (first == null)
				return null;
			else
				return first.item;
		} finally {
			takeLock.unlock();
		}
	}

	/**
	 * 从此队列移除指定元素的单个实例（如果存在）。
	 */
	public boolean remove(Object o) {
		if (o == null)
			return false;
		boolean removed = false;
		fullyLock();
		try {
			Node<E> trail = head;
			Node<E> p = head.next;
			while (p != null) {
				if (o.equals(p.item)) {
					removed = true;
					break;
				}
				trail = p;
				p = p.next;
			}
			if (removed) {
				p.item = null;
				trail.next = p.next;
				if (last == p)
					last = trail;
				if (count.getAndDecrement() == capacity)
					notFull.signalAll();
			}
		} finally {
			fullyUnlock();
		}
		return removed;
	}

	public int drainTo(Collection<? super E> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int drainTo(Collection<? super E> arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}