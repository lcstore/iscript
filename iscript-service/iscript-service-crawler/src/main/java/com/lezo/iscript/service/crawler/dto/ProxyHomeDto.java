package com.lezo.iscript.service.crawler.dto;

import java.io.Serializable;
import java.util.Date;

public class ProxyHomeDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String homeUrl;
	private String configParser;
	private Integer maxPage;
	private Integer isDelete;
	private Integer status;
	private Date createTime;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public Integer getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(Integer maxPage) {
		this.maxPage = maxPage;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
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

	public String getConfigParser() {
		return configParser;
	}

	public void setConfigParser(String configParser) {
		this.configParser = configParser;
	}

}
