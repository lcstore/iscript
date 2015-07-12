package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.lezo.iscript.common.UnifyValueAnnotation;

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
    private String tokenCategory;
    @UnifyValueAnnotation("0")
    private Integer priceRank;
    @UnifyValueAnnotation("0")
    private Integer saleRank;
    @UnifyValueAnnotation("0")
    private Integer commentRank;
    @UnifyValueAnnotation("0")
    private Integer baiduRank;
    @UnifyValueAnnotation("0")
    private Integer taobaoRank;
    private Date createTime;
    private Date updateTime;

}