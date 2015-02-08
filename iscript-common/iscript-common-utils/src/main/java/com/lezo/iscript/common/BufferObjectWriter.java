package com.lezo.iscript.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		synchronized (this.batchList) {
			this.batchList.addAll(dataList);
			if (this.batchList.size() >= capacity) {
				flush();
			}
		}
	}

	public void flush() {
		if (this.batchList.isEmpty()) {
			return;
		}
		List<E> copyList = Collections.emptyList();
		synchronized (this.batchList) {
			if (!this.batchList.isEmpty()) {
				copyList = new ArrayList<E>(this.batchList);
				for (int i = 0; i < this.batchList.size(); i++) {
					copyList.set(i, this.batchList.get(i));
				}
				this.batchList.clear();
			}
		}
		daoWriter.write(copyList);
	}
}
