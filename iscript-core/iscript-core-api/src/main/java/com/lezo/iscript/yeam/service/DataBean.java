package com.lezo.iscript.yeam.service;

import java.util.ArrayList;
import java.util.List;

public class DataBean {
	private List<Object> dataList = new ArrayList<Object>();
	private List<Object> nextList = new ArrayList<Object>();
	private List<Object> targetList = new ArrayList<Object>();

	public List<Object> getDataList() {
		return dataList;
	}

	public void setDataList(List<Object> dataList) {
		this.dataList = dataList;
	}

	public List<Object> getNextList() {
		return nextList;
	}

	public void setNextList(List<Object> nextList) {
		this.nextList = nextList;
	}

	public List<Object> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<Object> targetList) {
		this.targetList = targetList;
	}
}
