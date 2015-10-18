package com.lezo.iscript.service.crawler.dto;

import java.util.Date;
import java.util.UUID;

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
    public static final Integer ARBITER_NONE = 0;
    public static final Integer ARBITER_BARCODE = 10;
    public static final Integer ARBITER_SEARCH = 20;
    public static final Integer ARBITER_WARE = 30;
    public static final Integer ARBITER_NAME = 40;
    public static final Integer ARBITER_IMG = 50;
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
    /**
     * '仲裁者,0:不匹配，10:条码匹配，20：搜索匹配，30：同款匹配，40：商品名匹配，50：图像匹配'
     */
    private Integer arbiterId = ARBITER_NONE;
    private Integer similarScore;
    private String caption;
    private Integer confirmModel = CONFIRM_MODEL_AUTO;
    private Integer isDelete = 0;
    private String itemCode;
    private Date createTime;
    private Date updateTime;

    public static String newMatchCode() {
        String sHashCode = "" + UUID.randomUUID().toString().hashCode();
        return sHashCode.replace("-", "H");
    }

}