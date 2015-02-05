package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年2月5日
 */
public class ProxySeedDto {
	private Long id;
	private String url;
	private String createUrlsFun;
	private String decodePageFun;
	private Date createTime;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreateUrlsFun() {
		return createUrlsFun;
	}

	public void setCreateUrlsFun(String createUrlsFun) {
		this.createUrlsFun = createUrlsFun;
	}

	public String getDecodePageFun() {
		return decodePageFun;
	}

	public void setDecodePageFun(String decodePageFun) {
		this.decodePageFun = decodePageFun;
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
