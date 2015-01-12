package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.utils.InetAddressUtils;

public class ProxyAddrDto {
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_HTTP = 1;
	public static final int TYPE_SOCKET = 2;
	private Long id;
	private Long ip;
	private Integer port;
	private String addrCode;
	private String source;
	private String regionName;
	private String ispName;
	private Integer isDelete = 0;
	private Date createTime;
	private Date updateTime;
	private Integer type = TYPE_UNKNOWN;
	private String remark = "";

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

	public void setIpString(String ipString) {
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

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getIspName() {
		return ispName;
	}

	public void setIspName(String ispName) {
		this.ispName = ispName;
	}

	public String getAddrCode() {
		return addrCode;
	}

	public void setAddrCode(String addrCode) {
		this.addrCode = addrCode;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
