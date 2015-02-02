package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.common.CloneObject;

public class BrandShopDto extends CloneObject {
	private static final long serialVersionUID = 1L;
	public static final int TYPE_FLAGSHIP = 0;
	public static final int TYPE_SPECIALITY = 1;
	public static final int TYPE_EXCLUSIVE = 2;
	public static final int TYPE_UNKNOWN = 3;
	// shopType
	private Long id;
	private Integer siteId;
	private String brandCode;
	private String brandName;
	private String shopName;
	private String shopCode;
	private String shopUrl;
	/**
	 * 0-旗舰店，1-专卖店，2-专营店，3-其他
	 */
	private Integer shopType = TYPE_UNKNOWN;
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

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
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

	public Integer getShopType() {
		return shopType;
	}

	public void setShopType(Integer shopType) {
		this.shopType = shopType;
	}

}
