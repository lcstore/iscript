package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.BufferObjectWriter;
import com.lezo.iscript.common.ObjectWriter;

public class BufferWriterManager {
	private static Logger logger = LoggerFactory.getLogger(BufferWriterManager.class);
	private ConcurrentHashMap<String, BufferObjectWriter<?>> writerMap = new ConcurrentHashMap<String, BufferObjectWriter<?>>();
	private Object addLock = new Object();

	static class InstanceHolder {
		private static final BufferWriterManager INSTANCE = new BufferWriterManager();
	}

	private BufferWriterManager() {
	}

	public static BufferWriterManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public ObjectWriter<Object> getWriter(Class<?> dtoClass) {
		return getWriter(dtoClass.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public ObjectWriter<Object> getWriter(String name) {
		BufferObjectWriter<Object> writer = (BufferObjectWriter<Object>) writerMap.get(name);
		if (writer == null) {
			synchronized (addLock) {
				try {
					writer = (BufferObjectWriter<Object>) BufferWriterFactory.createBufferObjectWriter(name);
					BufferObjectWriter<Object> oldWriter = (BufferObjectWriter<Object>) writerMap.putIfAbsent(name, writer);
					writer = oldWriter == null ? writer : oldWriter;
				} catch (Exception e) {
					logger.error("add writer:" + name + ",cause:", e);
				}
			}
		}
		return writer;
	}

	public Iterator<Entry<String, BufferObjectWriter<?>>> iterator() {
		return writerMap.entrySet().iterator();
	}
}
