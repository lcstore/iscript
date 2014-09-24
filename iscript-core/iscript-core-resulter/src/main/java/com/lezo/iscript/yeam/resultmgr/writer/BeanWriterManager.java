package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.lezo.iscript.yeam.resultmgr.listener.ConsumeListener;

public class BeanWriterManager {
	private ConcurrentHashMap<String, ConsumeListener> listenerMap = new ConcurrentHashMap<String, ConsumeListener>();

	static class InstanceHolder {
		private static final BeanWriterManager INSTANCE = new BeanWriterManager();
	}

	private BeanWriterManager() {
	}

	public static BeanWriterManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void addListener(ConsumeListener listener) {
		listenerMap.put(listener.toString(), listener);
	}

	public ConsumeListener getListener(String name) {
		return listenerMap.get(name);
	}

	public Iterator<Entry<String, ConsumeListener>> iterator() {
		return listenerMap.entrySet().iterator();
	}
}
