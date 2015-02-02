package com.lezo.iscript.yeam.resultmgr;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rsf.ListItem;

public class DataFileWrapper {
	private String bucketName;
	private String domain = ".qiniudn.com";
	private ListItem item;
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

	public ListItem getItem() {
		return item;
	}

	public void setItem(ListItem item) {
		this.item = item;
	}

	public Mac getMac() {
		return mac;
	}

	public void setMac(Mac mac) {
		this.mac = mac;
	}
}
