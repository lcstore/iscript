package com.lezo.iscript.service.crawler.dto;

import java.io.Serializable;
import java.util.Date;

import com.lezo.iscript.utils.InetAddressUtils;

public class ProxyDetectDto implements Serializable {
	private static final long serialVersionUID = 1L;
	// 状态,-2:禁用，-1:停用，0:重试，1:可用,2:工作中
	public static final int STATUS_FORBIDDEN = -2;
	public static final int STATUS_NONUSE = -1;
	public static final int STATUS_RETRY = 0;
	public static final int STATUS_USABLE = 1;
	public static final int STATUS_WORK = 2;
	public static final int MAX_RETRY_TIMES = 3;
	private Long id;
	private Long ip;
	private int port;
	private String domain;
	private String url;
	private String detector;
	private Long curCost;
	private Long minCost;
	private Long maxCost;
	private int retryTimes;
	private int status = STATUS_RETRY;
	private Date createTime;
	private Date updateTime;

	private int failCount;
	private int successCount;
	private int lastSuccessCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIp() {
		return ip;
	}

	public String getIpString() {
		return InetAddressUtils.inet_ntoa(getIp());
	}

	public void setIp(Long ip) {
		this.ip = ip;
	}

	public void setIpString(String ipString) {
		setIp(InetAddressUtils.inet_aton(ipString));
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDetector() {
		return detector;
	}

	public void setDetector(String detector) {
		this.detector = detector;
	}

	public Long getCurCost() {
		return curCost;
	}

	public void setCurCost(Long curCost) {
		this.curCost = curCost;
	}

	public Long getMinCost() {
		return minCost;
	}

	public void setMinCost(Long minCost) {
		this.minCost = minCost;
	}

	public Long getMaxCost() {
		return maxCost;
	}

	public void setMaxCost(Long maxCost) {
		this.maxCost = maxCost;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getLastSuccessCount() {
		return lastSuccessCount;
	}

	public void setLastSuccessCount(int lastSuccessCount) {
		this.lastSuccessCount = lastSuccessCount;
	}

}
