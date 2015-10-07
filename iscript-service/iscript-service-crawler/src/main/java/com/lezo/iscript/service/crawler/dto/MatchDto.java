package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchDto {
    /**
     * 确认方式,0:自动，1:半自动，2：人工
     */
    public static final Integer CONFIRM_MODEL_AUTO = 0;
    public static final Integer CONFIRM_MODEL_SEMI = 1;
    public static final Integer CONFIRM_MODEL_PEOPLE = 2;
    private Long id;
    private String matchCode;
    private String wareCode;
    private String barCode;
    private String skuCode;
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
    private Integer similarScore;
    private String caption;
    private Integer confirmModel = CONFIRM_MODEL_AUTO;
    private Integer isDelete = 0;
    private String itemCode;
    private Date createTime;
    private Date updateTime;

}