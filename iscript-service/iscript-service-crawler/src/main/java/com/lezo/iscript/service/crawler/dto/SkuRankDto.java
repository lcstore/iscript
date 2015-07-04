package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkuRankDto {
	private Long id;
	private Long matchCode;
	private Integer siteId;
	private String productCode;
	private String productName;
	private String productUrl;
	private String imgUrl;
	private String tokenBrand;
	private String tokenGategory;
	private Integer priceRank;
	private Integer saleRank;
	private Integer commentRank;
	private Integer baiduRank;
	private Integer taobaoRank;
	private Date createTime;
	private Date updateTime;

}