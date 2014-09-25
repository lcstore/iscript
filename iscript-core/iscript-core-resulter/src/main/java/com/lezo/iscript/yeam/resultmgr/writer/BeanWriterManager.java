package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.lezo.iscript.common.ObjectWriter;

public class BeanWriterManager {
	private ConcurrentHashMap<String, ObjectWriter<Object>> writerMap = new ConcurrentHashMap<String, ObjectWriter<Object>>();

	static class InstanceHolder {
		private static final BeanWriterManager INSTANCE = new BeanWriterManager();
	}

	private BeanWriterManager() {
	}

	public static BeanWriterManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void addWriter(ObjectWriter<Object> writer) {
		writerMap.put(writer.toString(), writer);
	}

	public ObjectWriter<Object> getWriter(String name) {
		return writerMap.get(name);
	}

	public Iterator<Entry<String, ObjectWriter<Object>>> iterator() {
		return writerMap.entrySet().iterator();
	}
}
