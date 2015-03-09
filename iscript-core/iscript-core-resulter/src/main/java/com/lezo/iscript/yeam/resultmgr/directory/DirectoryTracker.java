package com.lezo.iscript.yeam.resultmgr.directory;

import java.util.HashMap;
import java.util.Map;

public class DirectoryTracker {
	private DirectoryDescriptor descriptor;
	private String marker = "";
	private int fileCount;
	private long stamp = 0;
	private Map<String, String> paramMap;

	public DirectoryTracker(DirectoryDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
		paramMap = new HashMap<String, String>();
		paramMap.put("marker", "");
	}

	public DirectoryDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(DirectoryDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public long getStamp() {
		return stamp;
	}

	public void setStamp(long stamp) {
		this.stamp = stamp;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
}
