package com.lezo.iscript.yeam.server.session;

public class ActionRecord {
	private long stamp = System.currentTimeMillis();
	private int index;
	private Object value;

	public long getStamp() {
		return stamp;
	}

	public Object getValue() {
		return value;
	}

	public int getIndex() {
		return index;
	}

	public void doRecord(Object value) {
		this.index++;
		this.stamp = System.currentTimeMillis();
		this.value = value;
	}
}
