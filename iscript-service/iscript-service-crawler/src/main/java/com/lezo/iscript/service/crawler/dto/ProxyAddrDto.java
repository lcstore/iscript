package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.utils.InetAddressUtils;

public class ProxyAddrDto {
	private Long id;
	private Long ip;
	private Integer port;
	private String source;
	private Integer isDelete = 0;
	private Date createTime;
	private Date updateTime;

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
		return InetAddressUtils.inet_ntoa(ip);
	}

	public void SetIpString(String ipString) {
		setIp(InetAddressUtils.inet_aton(ipString));
	}

	public void setIp(Long ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
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
