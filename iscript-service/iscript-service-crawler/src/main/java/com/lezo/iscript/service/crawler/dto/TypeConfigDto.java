package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

public class TypeConfigDto {
	public static final String TASKER_COMMON = "common";
	public static final int TYPE_ENABLE = 1;
	public static final int TYPE_DISABLE = 0;
	private Long id;
	private String type;
	private String tasker;
	private int minSize;
	private int maxSize;
	private int status;
	private Date createTime;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTasker() {
		return tasker;
	}

	public void setTasker(String tasker) {
		this.tasker = tasker;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
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

}
