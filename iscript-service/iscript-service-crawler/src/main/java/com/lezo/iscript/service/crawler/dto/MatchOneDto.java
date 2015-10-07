package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchOneDto {
	private Long id;
	private Integer merchantId;
	private String productId;
	private String productName;
	private String productCode;
	private String opponProductCode;
	private String opponProductName;
	private String opponProductUrl;
	private Integer siteId;
    private Float score;
	private Date createTime;
	private String creatorId;
	private Date updateTime;
	private Integer isManual;
	private Integer isAuto;
	private Integer isChampion;
	private Integer isCandidate;
	private Integer isConfirm;
	private Integer isDelete;
	private Integer formulaYmbols;
	private Integer formulaNumber;
	private String remarks;
	private Integer dataStatus;
	private Integer isBarcode;
	private Integer isDoubt;
	private Integer isSearch;
	private String instanceId;
	private Integer isInshop;

}