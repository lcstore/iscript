package com.lezo.iscript.utils;

import java.util.Iterator;
import java.util.List;

public class BatchIterator<E> implements Iterator<List<E>> {
	private List<E> itemList;
	private int batchSize;
	private int total;
	private int fromIndex;
	public BatchIterator(List<E> itemList) {
		this(itemList, 200);
	}

	public BatchIterator(List<E> itemList, int batchSize) {
		super();
		if(batchSize <1){
			throw new IllegalArgumentException("batch size must more than 1.but<"+batchSize+">");
		}
		this.itemList = itemList;
		this.batchSize = batchSize;
		if (itemList != null) {
			this.total = itemList.size();
		}
	}

	@Override
	public boolean hasNext() {
		return fromIndex < total;
	}

	@Override
	public List<E> next() {
		if(!hasNext()){
			return java.util.Collections.emptyList();
		}
		int toIndex = fromIndex + batchSize;
		toIndex = toIndex > total ? total : toIndex;
		List<E> subList = this.itemList.subList(fromIndex, toIndex);
		fromIndex = toIndex;
		return subList;
	}

	@Override
	public void remove() {

	}

	public List<E> getItemList() {
		return itemList;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public int getTotal() {
		return total;
	}

	public int getFromIndex() {
		return fromIndex;
	}

}
