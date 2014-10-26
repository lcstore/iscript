package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.lezo.iscript.common.ObjectWriter;

public class BufferWriterManager {
	private Map<String, ObjectWriter<?>> writerMap = new HashMap<String, ObjectWriter<?>>();
	private Object addLock = new Object();

	static class InstanceHolder {
		private static final BufferWriterManager INSTANCE = new BufferWriterManager();
	}

	private BufferWriterManager() {
	}

	public static BufferWriterManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void addWriter(String name) {
		try {
			ObjectWriter<?> writer = BufferWriterFactory.createBufferObjectWriter(name);
			addWriter(name, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addWriter(String name, ObjectWriter<?> writer) {
		if (name == null || writer == null) {
			return;
		}
		synchronized (addLock) {
			writerMap.put(name, writer);
		}
	}

	public ObjectWriter<Object> getWriter(Class<?> dtoClass) {
		return getWriter(dtoClass.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public ObjectWriter<Object> getWriter(String name) {
		ObjectWriter<Object> writer = (ObjectWriter<Object>) writerMap.get(name);
		if (writer == null) {
			addWriter(name);
			writer = (ObjectWriter<Object>) writerMap.get(name);
		}
		return writer;
	}

	public Iterator<Entry<String, ObjectWriter<?>>> iterator() {
		return writerMap.entrySet().iterator();
	}
}
