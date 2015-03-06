package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月6日
 */
public class ClientTokenDto {
	private Long id;
	private String clientType;
	private String clientId;
	private String clientSecret;
	private String clientParams;
	private String refreshToken;
	private String accessToken;
	private Date nextRefreshTime;
	private Date createTime;
	private Date updateTime;

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
		this.clientType = clientType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientParams() {
		return clientParams;
	}

	public void setClientParams(String clientParams) {
		this.clientParams = clientParams;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
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
}
