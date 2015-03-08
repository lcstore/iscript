package com.lezo.iscript.common.buffer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections4.CollectionUtils;

public class StampBeanBuffer<T> {
	private ConcurrentHashMap<String, T> beanMap = new ConcurrentHashMap<String, T>();
	private AtomicLong bufferStamp = new AtomicLong(0);

	public StampBeanBuffer() {
	}

	public synchronized void addAll(List<T> beanList, StampGetable<T> getable) {
		long curStamp = bufferStamp.get();
		long maxStamp = 0;
		for (T bean : beanList) {
			String name = getable.getName(bean);
			Long stamp = getable.getStamp(bean);
			if (curStamp < stamp) {
				beanMap.put(name, bean);
				maxStamp = stamp < maxStamp ? maxStamp : stamp;
			}
		}
		if (maxStamp > curStamp) {
			bufferStamp.set(maxStamp);
		}
	}

	public synchronized void addBean(T bean, StampGetable<T> getable) {
		String name = getable.getName(bean);
		Long stamp = getable.getStamp(bean);
		if (stamp > bufferStamp.get()) {
			bufferStamp.set(stamp);
		}
		beanMap.put(name, bean);
	}

	public T getBean(String name) {
		return beanMap.get(name);
	}

	public long getBufferStamp() {
		return bufferStamp.get();
	}

	public Iterator<Entry<String, T>> unmodifyIterator() {
		Collection<Entry<String, T>> unmodifyList = CollectionUtils.unmodifiableCollection(beanMap
				.entrySet());
		return unmodifyList.iterator();
	}
}
