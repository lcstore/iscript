package com.lezo.iscript.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class BufferObjectWriter<E> implements ObjectWriter<E> {
	private ObjectWriter<E> daoWriter;
	private List<E> batchList;
	private int capacity;

	public BufferObjectWriter(ObjectWriter<E> daoWriter, int capacity) {
		super();
		this.daoWriter = daoWriter;
		this.capacity = capacity;
		this.batchList = new ArrayList<E>(capacity);
	}

	@Override
	public void write(List<E> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		synchronized (this) {
			this.batchList.addAll(dataList);
			if (batchList.size() >= capacity) {
				flush();
			}
		}
	}

	public void flush() {
		List<E> copyList = batchList;
		if (copyList.isEmpty()) {
			return;
		}
		synchronized (this) {
			this.batchList = new ArrayList<E>(capacity);
		}
		daoWriter.write(copyList);
	}
}
