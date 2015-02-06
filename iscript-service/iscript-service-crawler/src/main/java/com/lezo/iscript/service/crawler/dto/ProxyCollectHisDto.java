package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.common.UnifyValueAnnotation;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年2月5日
 */
public class ProxyCollectHisDto {
	private Long id;
	@UnifyValueAnnotation("0")
	private Long seedId;
	@UnifyValueAnnotation("0")
	private Integer totalPage;
	@UnifyValueAnnotation("0")
	private Integer fetchPage;
	@UnifyValueAnnotation("0")
	private Integer totalCount;
	@UnifyValueAnnotation("0")
	private Integer newCount;
	private Date createTime;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSeedId() {
		return seedId;
	}

	public void setSeedId(Long seedId) {
		this.seedId = seedId;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getFetchPage() {
		return fetchPage;
	}

	public void setFetchPage(Integer fetchPage) {
		this.fetchPage = fetchPage;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getNewCount() {
		return newCount;
	}

	public void setNewCount(Integer newCount) {
		this.newCount = newCount;
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
