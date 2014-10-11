package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

public class PromotionMapDto {
	public static final int DELETE_FALSE = 0;
	public static final int DELETE_TRUE = 1;
	/**
	 * 状态，-1-促销未开始,0-促销中，1-促销结束
	 */
	public static final int PROMOTE_STATUS_WAIT = -1;
	public static final int PROMOTE_STATUS_START = 0;
	public static final int PROMOTE_STATUS_END = 1;
	/**
	 * 促销类型，-1-未知，0-满减,1-满赠，2-满折
	 */
	public static final int PROMOTE_TYPE_UNKONW = -1;
	public static final int PROMOTE_TYPE_FULL_SUB = 0;
	public static final int PROMOTE_TYPE_FULL_GIFT = 1;
	public static final int PROMOTE_TYPE_FULL_REBATE = 2;
	
	private Long id;
	private Integer siteId;
	private String productCode;
	private String promoteCode;
	private String promoteName;
	private String promoteDetail;
	private String promoteNums;
	private String promoteUrl;
	private Integer promoteType = PROMOTE_STATUS_START;
	private Integer promoteStatus = PROMOTE_STATUS_START;
	private Integer isDelete = DELETE_FALSE;
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

	public String getPromoteCode() {
		return promoteCode;
	}

	public void setPromoteCode(String promoteCode) {
		this.promoteCode = promoteCode;
	}

	public String getPromoteName() {
		return promoteName;
	}

	public void setPromoteName(String promoteName) {
		this.promoteName = promoteName;
	}

	public String getPromoteDetail() {
		return promoteDetail;
	}

	public void setPromoteDetail(String promoteDetail) {
		this.promoteDetail = promoteDetail;
	}

	public String getPromoteNums() {
		return promoteNums;
	}

	public void setPromoteNums(String promoteNums) {
		this.promoteNums = promoteNums;
	}

	public String getPromoteUrl() {
		return promoteUrl;
	}

	public void setPromoteUrl(String promoteUrl) {
		this.promoteUrl = promoteUrl;
	}

	public Integer getPromoteType() {
		return promoteType;
	}

	public void setPromoteType(Integer promoteType) {
		this.promoteType = promoteType;
	}

	public Integer getPromoteStatus() {
		return promoteStatus;
	}

	public void setPromoteStatus(Integer promoteStatus) {
		this.promoteStatus = promoteStatus;
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
