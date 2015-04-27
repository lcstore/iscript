package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.common.UnifyValueAnnotation;
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
	
	@UnifyValueAnnotation("0")
	private Integer mapType=0;
	@UnifyValueAnnotation("")
	private String mapLat;
	@UnifyValueAnnotation("")
	private String mapLng;
	@UnifyValueAnnotation("0")
	private Integer failCount;
	@UnifyValueAnnotation("0")
	private Integer successCount;
	@UnifyValueAnnotation("0")
	private Integer lastSuccessCount;
	@UnifyValueAnnotation("")
	private String regionCountry;
	@UnifyValueAnnotation("")
	private String regionCity;

	private Long seedId = 0L;

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

	public Long getSeedId() {
		return seedId;
	}

	public void setSeedId(Long seedId) {
		this.seedId = seedId;
	}

	public Integer getMapType() {
		return mapType;
	}

	public void setMapType(Integer mapType) {
		this.mapType = mapType;
	}

	public String getMapLat() {
		return mapLat;
	}

	public void setMapLat(String mapLat) {
		this.mapLat = mapLat;
	}

	public String getMapLng() {
		return mapLng;
	}

	public void setMapLng(String mapLng) {
		this.mapLng = mapLng;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getLastSuccessCount() {
		return lastSuccessCount;
	}

	public void setLastSuccessCount(Integer lastSuccessCount) {
		this.lastSuccessCount = lastSuccessCount;
	}

	public String getRegionCountry() {
		return regionCountry;
	}

	public void setRegionCountry(String regionCountry) {
		this.regionCountry = regionCountry;
	}

	public String getRegionCity() {
		return regionCity;
	}

	public void setRegionCity(String regionCity) {
		this.regionCity = regionCity;
	}

}
