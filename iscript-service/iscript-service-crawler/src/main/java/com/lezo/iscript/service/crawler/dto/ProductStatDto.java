package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import com.lezo.iscript.common.UnifyValueAnnotation;
import com.lezo.iscript.utils.PriceUtils;

import lombok.Data;

@Data
public class ProductStatDto {
	private Long id;
	private Integer shopId;
	private String productCode;
	private String productName;
	private String productUrl;
	private Long productPrice;
	private Long marketPrice;
	private Integer soldNum;
	private Integer commentNum;
	private Integer stockNum;
	private Date createTime;
	private Date updateTime;

	private Long minPrice;
	private Long maxPrice;

	private Integer siteId;
	private Integer goodComment;
	private Integer poorComment;

	public void setProductPrice(Float productPrice) {
		this.productPrice = PriceUtils.toCentPrice(productPrice);
	}

	public void setProductPrice(Long productPrice) {
		this.productPrice = productPrice;
	}

	public void setMarketPrice(Float marketPrice) {
		this.marketPrice = PriceUtils.toCentPrice(marketPrice);
	}

	public void setMarketPrice(Long marketPrice) {
		this.marketPrice = marketPrice;
	}

	public void setMinPrice(Float minPrice) {
		this.minPrice = PriceUtils.toCentPrice(minPrice);
	}

	public void setMinPrice(Long minPrice) {
		this.minPrice = minPrice;
	}

	public void setMaxPrice(Float maxPrice) {
		this.maxPrice = PriceUtils.toCentPrice(maxPrice);
	}

	public void setMaxPrice(Long maxPrice) {
		this.maxPrice = maxPrice;
	}

}
