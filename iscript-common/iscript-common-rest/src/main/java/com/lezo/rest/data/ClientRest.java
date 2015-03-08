package com.lezo.rest.data;

public class ClientRest {
	private String bucket;
	private String domain;
	private int capacity = 1;
	private DataRestable rester;

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public DataRestable getRester() {
		return rester;
	}

	public void setRester(DataRestable rester) {
		this.rester = rester;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("capacity must more than 1.");
		}
		this.capacity = capacity;
	}
}
