package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.lezo.iscript.utils.PriceUtils;

@Getter
@Setter
public class SimilarDto {
    /**
     * 仲裁者,0:不匹配，10:条码匹配，20：搜索匹配，30：同款匹配，40：商品名匹配，50：图像匹配
     */
    public static final Integer ARBITER_BARCODE = 10;
    public static final Integer ARBITER_SEARCH = 20;
    public static final Integer ARBITER_WARE = 30;
    public static final Integer ARBITER_NAME = 40;
    public static final Integer ARBITER_IMG = 50;

    private Long id;
    private String jobId;
    private String similarCode;
    private String skuCode;
    private String wareCode;
    private String barCode;
    private Integer siteId;
    private Integer shopId;
    private String productCode;
    private String productName;
    private String productUrl;
    private Long marketPrice;
    private String imgUrl;
    private String tokenCategory;
    private String tokenBrand;
    private String tokenModel;
    private String tokenUnit;
    private String tokenVary;
    private Integer arbiterId;
    private Date createTime;
    private Date updateTime;

    public void setMarketPrice(Float marketPrice) {
        this.marketPrice = PriceUtils.toCentPrice(marketPrice);
    }

    public void setMarketPrice(Long marketPrice) {
        this.marketPrice = marketPrice;
    }

}