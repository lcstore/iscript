package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

public class LuceneIndexDto {
	public static final int INDEX_DOING = 0;
	public static final int INDEX_DONE = 1;
	public static final int INDEX_ABORT = -1;
	private Long id;
	private String message;
	private Integer status;
	private Date dataDay;
	private Date createTime;
	private Date updateTime;

	private Integer dataCount;
	private Integer retry = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public Date getDataDay() {
		return dataDay;
	}

	public void setDataDay(Date dataDay) {
		this.dataDay = dataDay;
	}

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}

}
