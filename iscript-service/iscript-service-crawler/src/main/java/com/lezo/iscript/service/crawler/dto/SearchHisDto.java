package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

public class SearchHisDto {
	/**
	 * 新建：创建查询任务
	 */
	public static final int STATUS_NEW = 0;
	/**
	 * 搜索中：从索引中搜索结果
	 */
	public static final int STATUS_SEARCHING = 1;
	/**
	 * 完成：搜索完成，可以获取结果
	 */
	public static final int STATUS_DONE = 2;
	/**
	 * 中断：搜索失败，其他原因中断
	 */
	public static final int STATUS_ABORT = -1;

	public static final long EXPIRED_TIME = 24 * 60 * 60 * 1000;
	private Long id;
	private String queryWord;
	private String querySolr;
	private String queryResult;
	private Long queryCost;
	private Integer queryHit = 0;
	private Integer status = STATUS_NEW;
	private Date createTime;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQueryWord() {
		return queryWord;
	}

	public void setQueryWord(String queryWord) {
		this.queryWord = queryWord;
	}

	public String getQuerySolr() {
		return querySolr;
	}

	public void setQuerySolr(String querySolr) {
		this.querySolr = querySolr;
	}

	public String getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(String queryResult) {
		this.queryResult = queryResult;
	}

	public Long getQueryCost() {
		return queryCost;
	}

	public void setQueryCost(Long queryCost) {
		this.queryCost = queryCost;
	}

	public Integer getQueryHit() {
		return queryHit;
	}

	public void setQueryHit(Integer queryHit) {
		this.queryHit = queryHit;
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

}
