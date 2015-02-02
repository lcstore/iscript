package com.lezo.iscript.yeam.resultmgr.directory;

public class DirectoryTracker {
	private DirectoryDescriptor descriptor;
	private String marker = "";
	private int fileCount;
	private long stamp = 0;

	public DirectoryTracker(DirectoryDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
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
}
