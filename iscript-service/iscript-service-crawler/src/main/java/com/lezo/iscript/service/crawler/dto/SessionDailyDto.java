package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

public class SessionDailyDto {
	private Long id;
	private String clienName;
	private Integer requestSize;
	private Integer responeSize;
	private Integer errorSize;
	private Integer successNum;
	private Integer failNum;
	private Integer workTime;
	private Integer setupTimes;
	private Date createTime;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClienName() {
		return clienName;
	}

	public void setClienName(String clienName) {
		this.clienName = clienName;
	}

	public Integer getRequestSize() {
		return requestSize;
	}

	public void setRequestSize(Integer requestSize) {
		this.requestSize = requestSize;
	}

	public Integer getResponeSize() {
		return responeSize;
	}

	public void setResponeSize(Integer responeSize) {
		this.responeSize = responeSize;
	}

	public Integer getErrorSize() {
		return errorSize;
	}

	public void setErrorSize(Integer errorSize) {
		this.errorSize = errorSize;
	}

	public Integer getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(Integer successNum) {
		this.successNum = successNum;
	}

	public Integer getFailNum() {
		return failNum;
	}

	public void setFailNum(Integer failNum) {
		this.failNum = failNum;
	}

	public Integer getWorkTime() {
		return workTime;
	}

	public void setWorkTime(Integer workTime) {
		this.workTime = workTime;
	}

	public Integer getSetupTimes() {
		return setupTimes;
	}

	public void setSetupTimes(Integer setupTimes) {
		this.setupTimes = setupTimes;
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
