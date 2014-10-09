package com.lezo.iscript.yeam.writable;

import java.io.Serializable;

public class StorageHeaderWritable implements Serializable {
	private static final long serialVersionUID = 420678970779882378L;
	private String name;
	private String path;
	private long length;
	private long offset;
	private int limit;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

}
