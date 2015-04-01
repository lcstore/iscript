package com.lezo.iscript.service.crawler.dto;

import java.util.Date;
import java.util.UUID;

import com.lezo.iscript.common.CloneObject;

public class BrandDto extends CloneObject<BrandDto> {
	private Long id;
	private Integer siteId;
	private String brandCode;
	private String brandName;
	private String brandUrl;
	private String synonymCode;
	private String region;
	private Date createTime;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getBrandUrl() {
		return brandUrl;
	}

	public void setBrandUrl(String brandUrl) {
		this.brandUrl = brandUrl;
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

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSynonymCode() {
		return synonymCode;
	}

	public void setSynonymCode(String synonymCode) {
		this.synonymCode = synonymCode;
	}

	public static String randomSynonymCode() {
		String synCode = UUID.randomUUID().toString();
		int hCode = synCode.hashCode();
		if (hCode < 0) {
			return "H" + (-hCode);
		}
		return "" + hCode;
	}

}
