package com.lezo.iscript.utils.queue;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HierarchyQueue<E> implements Comparable<HierarchyQueue<E>>,
		FairPriorityable {
	private final String name;
	private final int level;
	private Queue<E> queue;

	public HierarchyQueue(String name, int level, Queue<E> queue) {
		super();
		this.name = name;
		this.level = level;
		this.queue = queue;
	}

	public void offer(E e) {
		this.queue.offer(e);
	}

	public E poll() {
		return this.queue.poll();
	}

	public int size() {
		return this.queue.size();
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public int compareTo(HierarchyQueue<E> o) {
		int sub = level - o.getLevel();
		int size = size();
		int oSize = o.size();
		if (oSize == 0 && size > 0) {
			return -1;
		}
		if (size == 0 && oSize > 0) {
			return 1;
		}
		int result = (sub != 0) ? sub : o.size() - size();
		return result;
	}

	@Override
	public String toString() {
		return "HierarchyQueue [name=" + name + ", level=" + level + ", size="
				+ size() + "]";
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(level).append(name).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HierarchyQueue<E> other = (HierarchyQueue<E>) obj;
		return new EqualsBuilder().append(level, other.getLevel())
				.append(name, other.getName()).isEquals();
	}

	public String getKey() {
		String key = this.level + ":" + (null == this.name ? "" : this.name);
		return key;
	}

	public HierarchyQueue<E> move() throws Exception {
		HierarchyQueue<E> moveQueue = new HierarchyQueue<E>(name, level, queue);
		this.queue = createQueue();
		return moveQueue;
	}

	private Queue<E> createQueue() {
		Queue<E> newQueue = null;
		if (this.queue instanceof ArrayBlockingQueue<?>) {
			newQueue = new ArrayBlockingQueue<E>(100);
		} else if (this.queue instanceof LinkedBlockingQueue<?>) {
			newQueue = new LinkedBlockingQueue<E>();
		}
		return newQueue;
	}

}
