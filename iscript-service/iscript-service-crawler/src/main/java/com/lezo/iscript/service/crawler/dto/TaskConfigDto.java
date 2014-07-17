package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

public class TaskConfigDto {
	public static final int STATUS_ENABLE = 1;
	public static final int STATUS_DISABLE = 0;
	public static final int DEST_CONFIG = 0;
	public static final int DEST_STRATEGY = 1;
	private String id;
	private String type;
	private String config;
	private String source;
	private int status;
	private int destType = DEST_CONFIG;
	private Date createTime;
	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getDestType() {
		return destType;
	}

	public void setDestType(int destType) {
		this.destType = destType;
	}
}
