package com.lezo.iscript.yeam.resultmgr.listener;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumeListenerManager {
	private static final ConsumeListenerManager INSTANCE = new ConsumeListenerManager();
	private ConcurrentHashMap<String, ConsumeListener> listenerMap = new ConcurrentHashMap<String, ConsumeListener>();

	private ConsumeListenerManager() {
		addListener(new BeanCopyListener());
	}

	public static ConsumeListenerManager getInstance() {
		return INSTANCE;
	}

	public void addListener(ConsumeListener listener) {
		listenerMap.put(listener.getClass().getSimpleName(), listener);
	}

	public ConsumeListener getListener(String name) {
		return listenerMap.get(name);
	}

	public Iterator<Entry<String, ConsumeListener>> iterator() {
		return listenerMap.entrySet().iterator();
	}
}
