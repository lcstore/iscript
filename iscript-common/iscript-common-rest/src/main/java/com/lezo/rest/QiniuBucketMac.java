package com.lezo.rest;

import com.qiniu.api.auth.digest.Mac;

public class QiniuBucketMac {
	private Mac mac;
	private String bucket;
	private Integer capacity;
	private String domain = "qiniudn.com";

	public QiniuBucketMac(Mac mac, String bucket, Integer capacity, String domain) {
		super();
		this.mac = mac;
		this.bucket = bucket;
		this.capacity = capacity;
		this.domain = domain;
	}

	public QiniuBucketMac(Mac mac, String bucket, Integer capacity) {
		this(mac, bucket, capacity, bucket + ".qiniudn.com");
	}

	public Mac getMac() {
		return mac;
	}

	public void setMac(Mac mac) {
		this.mac = mac;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
