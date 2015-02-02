package com.lezo.iscript.yeam.resultmgr.directory;

import java.util.Date;

public class DirectoryDescriptor {
	private final String bucketName;
	private final String dataPath;
	private final String directoryKey;
	private final String domain;
	private Date createTime;

	public DirectoryDescriptor(String dataPath, String bucketName, String domain) {
		super();
		this.bucketName = bucketName;
		this.dataPath = dataPath;
		this.domain = domain;
		StringBuilder sb = new StringBuilder();
		sb.append(this.bucketName);
		sb.append(".");
		sb.append(this.domain);
		sb.append(":");
		sb.append(this.dataPath);
		this.directoryKey = sb.toString();
	}

	public DirectoryDescriptor(String dataPath, String bucketName) {
		this(dataPath, bucketName, "qiniudn.com");
	}

	public String getDomain() {
		return domain;
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getDataPath() {
		return dataPath;
	}

	public String getDirectoryKey() {
		return directoryKey;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
