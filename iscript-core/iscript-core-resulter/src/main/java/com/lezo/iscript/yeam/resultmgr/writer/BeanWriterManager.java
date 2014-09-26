package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.lezo.iscript.common.BufferObjectWriter;
import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public class BeanWriterManager {
	private ConcurrentHashMap<String, ObjectWriter<?>> writerMap = new ConcurrentHashMap<String, ObjectWriter<?>>();

	static class InstanceHolder {
		private static final BeanWriterManager INSTANCE = new BeanWriterManager();
	}

	private BeanWriterManager() {
		addWriter(new BufferObjectWriter<ProductDto>(new ProductWriter(), 200));
		addWriter(new BufferObjectWriter<ProductStatDto>(new ProductStatWriter(), 200));
	}

	public static BeanWriterManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void addWriter(ObjectWriter<?> writer) {
		writerMap.put(writer.toString(), writer);
	}

	@SuppressWarnings("unchecked")
	public ObjectWriter<Object> getWriter(String name) {
		return (ObjectWriter<Object>) writerMap.get(name);
	}

	public Iterator<Entry<String, ObjectWriter<?>>> iterator() {
		return writerMap.entrySet().iterator();
	}
}
