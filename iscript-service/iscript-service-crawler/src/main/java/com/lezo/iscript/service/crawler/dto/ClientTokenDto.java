package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.common.UnifyValueAnnotation;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月6日
 */
public class ClientTokenDto {
	private Long id;
	@UnifyValueAnnotation("")
	private String clientType;
	private String clientBucket;
	private String clientDomain;
	@UnifyValueAnnotation("")
	private String clientKey;
	@UnifyValueAnnotation("")
	private String clientSecret;
	@UnifyValueAnnotation("")
	private String clientParams;
	@UnifyValueAnnotation("")
	private String refreshToken;
	@UnifyValueAnnotation("")
	private String accessToken;
	private Date nextRefreshTime;
	private Date createTime;
	private Date updateTime;

	@UnifyValueAnnotation("0")
	private Integer successCount = 0;
	@UnifyValueAnnotation("0")
	private Integer failCount = 0;
	@UnifyValueAnnotation("")
	private String lastMessge;

	@UnifyValueAnnotation("0")
	private Integer isDelete = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = trimString(clientType);
	}

	public String getClientBucket() {
		return clientBucket;
	}

	public void setClientBucket(String clientBucket) {
		this.clientBucket = trimString(clientBucket);
	}

	public String getClientDomain() {
		return clientDomain;
	}

	public void setClientDomain(String clientDomain) {
		this.clientDomain = trimString(clientDomain);
	}

	public String getClientKey() {
		return clientKey;
	}

	public void setClientKey(String clientKey) {
		this.clientKey = trimString(clientKey);
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = trimString(clientSecret);
	}

	public String getClientParams() {
		return clientParams;
	}

	public void setClientParams(String clientParams) {
		this.clientParams = trimString(clientParams);
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = trimString(refreshToken);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = trimString(accessToken);
	}

	public Date getNextRefreshTime() {
		return nextRefreshTime;
	}

	public void setNextRefreshTime(Date nextRefreshTime) {
		this.nextRefreshTime = nextRefreshTime;
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

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	public String getLastMessge() {
		return lastMessge;
	}

	public void setLastMessge(String lastMessge) {
		this.lastMessge = lastMessge;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	private String trimString(String source) {
		return source == null ? null : source.trim();
	}
}
