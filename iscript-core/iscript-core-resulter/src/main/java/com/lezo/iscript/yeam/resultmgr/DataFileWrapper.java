package com.lezo.iscript.yeam.resultmgr;

import com.lezo.rest.data.RestFile;
import com.qiniu.api.auth.digest.Mac;

public class DataFileWrapper {
	private String bucketName;
	private String domain = ".qiniudn.com";
	private RestFile item;
	private Mac mac;

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public RestFile getItem() {
		return item;
	}

	public void setItem(RestFile item) {
		this.item = item;
	}

	public Mac getMac() {
		return mac;
	}

	public void setMac(Mac mac) {
		this.mac = mac;
	}
}
