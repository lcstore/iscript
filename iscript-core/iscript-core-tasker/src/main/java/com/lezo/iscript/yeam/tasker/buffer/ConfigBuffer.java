package com.lezo.iscript.yeam.tasker.buffer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ConfigBuffer {
	private ConcurrentHashMap<String, ConfigWritable> configMap = new ConcurrentHashMap<String, ConfigWritable>();
	private long stamp = 0;

	private ConfigBuffer() {
	}

	private static final class InstanceHolder {
		private static final ConfigBuffer INSTANCE = new ConfigBuffer();
	}

	public static ConfigBuffer getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public synchronized void addConfig(String name, ConfigWritable configWritable) {
		configMap.put(name, configWritable);
		stamp = configWritable.getStamp();
	}

	public ConfigWritable getConfig(String name) {
		return configMap.get(name);
	}

	public long getStamp() {
		return stamp;
	}

	public Iterator<Entry<String, ConfigWritable>> unmodifyIterator() {
		Collection<Entry<String, ConfigWritable>> unmodifyList = CollectionUtils.unmodifiableCollection(configMap
				.entrySet());
		return unmodifyList.iterator();
	}
}
