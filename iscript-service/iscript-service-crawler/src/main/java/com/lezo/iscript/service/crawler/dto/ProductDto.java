package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.common.UnifyValueAnnotation;
import com.lezo.iscript.utils.PriceUtils;

import lombok.Data;

@Data
public class ProductDto {
	private Long id;
	private Integer shopId;
	private String productCode;
	private String productName;
	private Long marketPrice;
	private String productUrl;
	private String productBrand;
	private String productModel;
	private String productAttr;
	private String barCode;
	private String imgUrl;
	private String unionUrl;
	private Date onsailTime;
	private Date createTime;
	private Date updateTime;

	private Integer siteId;
	@UnifyValueAnnotation("")
	private String categoryNav;
	@UnifyValueAnnotation("")
	private String tokenBrand;
	@UnifyValueAnnotation("")
	private String tokenCategory;

	@UnifyValueAnnotation("")
	private String spuCodes;
	@UnifyValueAnnotation("")
	private String spuVary;

	public void setMarketPrice(Float price) {
		this.marketPrice = PriceUtils.toCentPrice(price);
	}

	public void setMarketPrice(Long centPrice) {
		this.marketPrice = centPrice;
	}
}
