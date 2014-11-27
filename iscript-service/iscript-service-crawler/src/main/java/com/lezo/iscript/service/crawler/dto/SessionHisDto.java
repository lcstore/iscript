package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

public class SessionHisDto {
	public static final String SESSION_ID = "sid";
	public static final String CLIEN_NAME = "name";
	public static final String REQUEST_SIZE = "request";
	public static final String RESPONE_SIZE = "respone";
	public static final String ERROR_SIZE = "error";
	public static final String SUCCESS_NUM = "success";
	public static final String FAIL_NUM = "fail";
	public static final String LOSE_TIME = "lostTime";
	public static final String PUSH_STAMP = "stamp";
	public static final long MAX_SAVE_INTERVAL = 5 * 60 * 1000;

	public static final int STATUS_UP = 1;
	public static final int STATUS_DOWN = 0;
	public static final int STATUS_INTERRUPT = -1;

	private Long id;
	private String sessionId;
	private String clienName;
	private Integer requestSize;
	private Integer responeSize;
	private Integer errorSize;
	private Integer successNum;
	private Integer failNum;
	private Integer status;
	private Date createTime;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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
		this.requestSize = getDefaultIfNull(requestSize);
	}

	public Integer getResponeSize() {
		return responeSize;
	}

	public void setResponeSize(Integer responeSize) {
		this.responeSize = getDefaultIfNull(responeSize);
	}

	public Integer getErrorSize() {
		return errorSize;
	}

	public void setErrorSize(Integer errorSize) {
		this.errorSize = getDefaultIfNull(errorSize);
	}

	public Integer getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(Integer successNum) {
		this.successNum = getDefaultIfNull(successNum);
	}

	public Integer getFailNum() {
		return failNum;
	}

	public void setFailNum(Integer failNum) {
		this.failNum = getDefaultIfNull(failNum);
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
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

	private int getDefaultIfNull(Integer value) {
		return value == null ? 0 : value;
	}

	@Override
	public String toString() {
		return String.format("client:%s,request:%d,respone:%d,error:%d,success:%d,fail:%d", getClienName(),
				getRequestSize(), getResponeSize(), getErrorSize(), getSuccessNum(), getFailNum());
	}
}
