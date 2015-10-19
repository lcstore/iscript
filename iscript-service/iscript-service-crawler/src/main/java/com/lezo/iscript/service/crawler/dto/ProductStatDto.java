package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.lezo.iscript.utils.PriceUtils;

@Getter
@Setter
public class ProductStatDto {
    private Long id;
    private String skuCode;
    private Integer siteId;
    private Integer shopId;
    private String productCode;
    private String productName;
    private String productUrl;
    private String categoryNav;
    private Long minPrice;
    private Long maxPrice;
    private Long productPrice;
    private Long marketPrice;
    private Integer soldNum;
    private Integer commentNum;
    private Integer goodComment;
    private Integer poorComment;
    private Integer stockNum;
    private Date createTime;
    private Date updateTime;

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