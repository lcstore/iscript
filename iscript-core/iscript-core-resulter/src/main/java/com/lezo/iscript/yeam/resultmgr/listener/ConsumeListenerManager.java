package com.lezo.iscript.yeam.resultmgr.listener;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumeListenerManager {
	private ConcurrentHashMap<String, ConsumeListener> listenerMap = new ConcurrentHashMap<String, ConsumeListener>();

	static class InstanceHolder {
		private static final ConsumeListenerManager INSTANCE = new ConsumeListenerManager();
	}

	private ConsumeListenerManager() {
		addListener(new BeanCopyListener());
	}

	public static ConsumeListenerManager getInstance() {
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
