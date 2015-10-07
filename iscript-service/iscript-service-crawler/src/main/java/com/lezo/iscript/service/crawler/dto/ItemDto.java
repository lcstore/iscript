package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {
	private Long id;
	private String matchCode;
	private String wareCode;
	private String barCode;
	private String skuCode;
	private String productCode;
	private String productName;
	private String productUrl;
	private String imgUrl;
	private String tokenCategory;
	private String tokenBrand;
	private String tokenUnit;
	private String tokenModel;
	private String tokenVary;
	private Long minPrice;
	private Long maxPrice;
	private Date createTime;
	private Date updateTime;

}