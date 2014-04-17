package com.lezo.iscript.yeam.writable;

import java.io.Serializable;

public class StorageBufferWritable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6322328831669610154L;
	private String name;
	private byte[] buffer;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

}
