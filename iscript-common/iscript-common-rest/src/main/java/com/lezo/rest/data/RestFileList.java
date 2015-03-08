package com.lezo.rest.data;

import java.util.List;

public class RestFileList {
	private List<RestFile> dataList;
	private boolean isEOF = false;
	private String marker;

	public List<RestFile> getDataList() {
		return dataList;
	}

	public void setDataList(List<RestFile> dataList) {
		this.dataList = dataList;
	}

	public boolean isEOF() {
		return isEOF;
	}

	public void setEOF(boolean isEOF) {
		this.isEOF = isEOF;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}
}
