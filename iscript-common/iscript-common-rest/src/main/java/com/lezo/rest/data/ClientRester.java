package com.lezo.rest.data;

public class ClientRester {
	private String bucket;
	private String domain;
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
}
