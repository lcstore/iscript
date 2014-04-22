package com.lezo.iscript.yeam.writable;

import java.io.Serializable;

public class ConfigWritable implements Serializable {
	public static final int CONFIG_TYPE_SCRIPT = 0;
	public static final int CONFIG_TYPE_JAVA = 1;
	public static final int CONFIG_TYPE_UNKNOWN = -1;
	private static final long serialVersionUID = -8951774607408237258L;
	private String name;
	private int type;
	private byte[] content;
	private long stamp;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStamp() {
		return stamp;
	}

	public void setStamp(long stamp) {
		this.stamp = stamp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
