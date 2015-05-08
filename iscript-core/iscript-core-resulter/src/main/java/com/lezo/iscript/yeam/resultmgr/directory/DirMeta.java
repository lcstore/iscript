package com.lezo.iscript.yeam.resultmgr.directory;

import java.util.Calendar;
import java.util.Date;

public class DirMeta {
	private static final String DIR_SEPARATOR = "/";
	private String baseDir = "iscript";
	private String type;
	private String pid;
	private Date createTime;
	private String bucket;
	private String domain;
	private String dirPath;
	private String dirKey;

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String toDirPath() {
		if (this.dirPath != null) {
			return this.dirPath;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseDir());
		sb.append(DIR_SEPARATOR);
		sb.append(format(getCreateTime()));
		sb.append(DIR_SEPARATOR);
		sb.append(getType());
		sb.append(DIR_SEPARATOR);
		sb.append(getPid());
		this.dirPath = sb.toString();
		return this.dirPath;
	}

	private String format(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int days = c.get(Calendar.DAY_OF_MONTH);
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append(addHeadZero(month));
		sb.append(addHeadZero(days));
		return sb.toString();
	}

	private String addHeadZero(int data) {
		return data < 10 ? "0" + data : "" + data;
	}

	public String toDirKey() {
		if (this.dirKey != null) {
			return this.dirKey;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getDomain());
		sb.append(DIR_SEPARATOR);
		sb.append(getBucket());
		sb.append(DIR_SEPARATOR);
		sb.append(toDirPath());
		this.dirKey = sb.toString();
		return this.dirKey;
	}
}
