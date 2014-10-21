package com.lezo.iscript.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class BufferObjectWriter<E> implements ObjectWriter<E> {
	private ObjectWriter<E> daoWriter;
	private List<E> bList;
	private Object lock;
	private int bSize;

	public BufferObjectWriter(ObjectWriter<E> daoWriter, int bSize) {
		super();
		this.daoWriter = daoWriter;
		this.bSize = bSize;
		this.bList = new ArrayList<E>(bSize);
		this.lock = this;
	}

	@Override
	public void write(List<E> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		synchronized (lock) {
			List<E> remainList = dataList;
			int remain = bSize - bList.size();
			while (remain <= remainList.size()) {
				addBuffer(remainList, 0, remain);
				flush();
				remainList = remainList.subList(remain, remainList.size());
			}
			addBuffer(remainList, 0, remainList.size());
		}
	}

	private void addBuffer(List<E> dataList, int offset, int len) {
		for (int index = offset, size = dataList.size(); index < len && index < size; index++) {
			bList.add(dataList.get(index));
		}
	}

	@Override
	public void flush() {
		synchronized (lock) {
			if (bList.isEmpty()) {
				return;
			}
			daoWriter.write(bList);
			this.bList = new ArrayList<E>(this.bSize);
		}
	}
}
