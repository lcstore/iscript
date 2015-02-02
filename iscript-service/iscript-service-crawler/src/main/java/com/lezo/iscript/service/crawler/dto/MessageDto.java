package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.common.CloneObject;

public class MessageDto extends CloneObject<MessageDto> {
	public static final int NEW_MESSGE = 0;
	private Long id;
	private String name;
	private String message;
	private String source;
	private String remark;
	private Integer status;
	private Integer sortCode;
	private Date createTime;
	private Date updateTime;

	private String dataBucket;
	private String dataDomain;
	private int dataCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSortCode() {
		return sortCode;
	}

	public void setSortCode(Integer sortCode) {
		this.sortCode = sortCode;
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

	public String getDataBucket() {
		return dataBucket;
	}

	public void setDataBucket(String dataBucket) {
		this.dataBucket = dataBucket;
	}

	public String getDataDomain() {
		return dataDomain;
	}

	public void setDataDomain(String dataDomain) {
		this.dataDomain = dataDomain;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

}
