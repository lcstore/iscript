package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ProductStandardDto {
	private Long id;
	private Long similarCode;
	private Integer siteId;
	private Integer shopId;
	private String productCode;
	private String productName;
	private String productUrl;
	private String barCode;
	private String imgUrl;
	private String tokenBrand;
	private String tokenGategory;
	private Date createTime;
	private Date updateTime;
}
