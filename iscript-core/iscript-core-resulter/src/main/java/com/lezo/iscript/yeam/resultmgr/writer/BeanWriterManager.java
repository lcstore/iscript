package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import com.lezo.iscript.common.BufferObjectWriter;
import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public class BeanWriterManager {
	private WeakHashMap<String, ObjectWriter<?>> writerMap = new WeakHashMap<String, ObjectWriter<?>>();

	static class InstanceHolder {
		private static final BeanWriterManager INSTANCE = new BeanWriterManager();
	}

	private BeanWriterManager() {
		addWriter("ProductDto", new BufferObjectWriter<ProductDto>(new ProductWriter(), 200));
		addWriter("ProductStatDto", new BufferObjectWriter<ProductStatDto>(new ProductStatWriter(), 200));
	}

	public static BeanWriterManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public synchronized void addWriter(String name, ObjectWriter<?> writer) {
		writerMap.put(name, writer);
	}

	@SuppressWarnings("unchecked")
	public ObjectWriter<Object> getWriter(String name) {
		return (ObjectWriter<Object>) writerMap.get(name);
	}

	public Iterator<Entry<String, ObjectWriter<?>>> iterator() {
		return writerMap.entrySet().iterator();
	}
}
