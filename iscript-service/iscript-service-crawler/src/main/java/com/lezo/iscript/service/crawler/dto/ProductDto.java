package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.lezo.iscript.utils.PriceUtils;

@Getter
@Setter
public class ProductDto {
    private Long id;
    private String skuCode;
    private Integer siteId;
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
    private String categoryNav;
    private String tokenBrand;
    private String tokenCategory;
    private String spuCodes;
    private String spuVary;

    public void setMarketPrice(Float marketPrice) {
        this.marketPrice = PriceUtils.toCentPrice(marketPrice);
    }

    public void setMarketPrice(Long marketPrice) {
        this.marketPrice = marketPrice;
    }

}