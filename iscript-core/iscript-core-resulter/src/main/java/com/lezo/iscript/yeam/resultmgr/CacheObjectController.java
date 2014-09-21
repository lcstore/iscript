package com.lezo.iscript.yeam.resultmgr;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class CacheObjectController {
	private ConcurrentHashMap<String, CacheObject> keyMap = new ConcurrentHashMap<String, CacheObject>();
	private volatile long timeOut = getNextTimeOut();

	private CacheObjectController() {
	}

	static class InstanceHolder {
		static final CacheObjectController INSTANCE = new CacheObjectController();
	}

	public static CacheObjectController getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public CacheObject getValidValue(String key) {
		CacheObject value = keyMap.get(key);
		if (isGolbalTimeOut()) {
			clearTimeOutValue();
		}
		return value;
	}

	public void addValidValue(String key, CacheObject value) {
		keyMap.putIfAbsent(key, value);
	}

	private boolean isGolbalTimeOut() {
		return System.currentTimeMillis() > timeOut;
	}

	private long getNextTimeOut() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTimeInMillis();
	}

	private synchronized void clearTimeOutValue() {
		if (!isGolbalTimeOut()) {
			return;
		}
		Iterator<Entry<String, CacheObject>> it = keyMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, CacheObject> value = it.next();
			if (value.getValue().isTimeOut()) {
				it.remove();
			}
		}
		timeOut = getNextTimeOut();
	}

}
