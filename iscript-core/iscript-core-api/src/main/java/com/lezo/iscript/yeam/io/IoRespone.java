package com.lezo.iscript.yeam.io;

import java.io.Serializable;

public class IoRespone implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6135902219852876965L;
	private int type;
	private long timeMills = System.currentTimeMillis();
	private Object data;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public long getTimeMills() {
		return timeMills;
	}

	public void setTimeMills(long timeMills) {
		this.timeMills = timeMills;
	}
}
