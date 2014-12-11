package com.lezo.iscript.service.crawler.dto;

import java.io.Serializable;
import java.util.Date;

public class PromotionTrackDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Integer siteId;
	private String productCode;
	private String productName;
	private String productUrl;
	private Float fromPrice;
	private Float targetPrice;
	private Float toPrice;
	private String promotionDetail;
	private Date fromTime;
	private Date toTime;
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

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public Float getFromPrice() {
		return fromPrice;
	}

	public void setFromPrice(Float fromPrice) {
		this.fromPrice = fromPrice;
	}

	public Float getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(Float targetPrice) {
		this.targetPrice = targetPrice;
	}

	public Float getToPrice() {
		return toPrice;
	}

	public void setToPrice(Float toPrice) {
		this.toPrice = toPrice;
	}

	public String getPromotionDetail() {
		return promotionDetail;
	}

	public void setPromotionDetail(String promotionDetail) {
		this.promotionDetail = promotionDetail;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
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
