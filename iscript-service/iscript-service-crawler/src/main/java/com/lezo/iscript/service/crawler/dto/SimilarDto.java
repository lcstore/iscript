package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Data;

import com.lezo.iscript.common.UnifyValueAnnotation;
import com.lezo.iscript.utils.PriceUtils;

@Data
public class SimilarDto {
	public static final int WARE_TYPE_AUTO = 0;
	public static final int WARE_TYPE_SELECTIVE = 1;
	public static final int WARE_TYPE_CONFIRM = 2;
	public static final int CONFIRM_MODEL_AUTO = 0;
	public static final int CONFIRM_MODEL_SELECTIVE = 1;
	public static final int CONFIRM_MODEL_CONFIRM = 2;
	// 仲裁者,0:不匹配，10:商品名匹配，11：搜索匹配，21：条码匹配，22：同款匹配
	public static final int ARBITER_ID_UNMATCH = 0;
	public static final int ARBITER_ID_NAME = 10;
	public static final int ARBITER_ID_SEARCH = 11;
	public static final int ARBITER_ID_BARCODE = 21;
	public static final int ARBITER_ID_STYLE = 22;
	private Long id;
	private Integer wareType = WARE_TYPE_AUTO;
	private Long wareCode;
	private Long similarCode;
	private Integer siteId;
	private Integer shopId;
	private String productCode;
	private String productName;
	private String productUrl;
	private Long marketPrice;
	private String barCode;
	private String imgUrl;
	@UnifyValueAnnotation("")
	private String tokenBrand;
	@UnifyValueAnnotation("")
	private String tokenCategory;
	@UnifyValueAnnotation("")
	private String tokenVary;
	@UnifyValueAnnotation("")
	private String deciderKvs;
	private Integer arbiterId = ARBITER_ID_NAME;
	private Integer similarScore;
	@UnifyValueAnnotation("")
	private String caption;
	private Integer confirmModel = CONFIRM_MODEL_AUTO;
	private Integer isStandard = 0;
	private Date createTime;
	private Date updateTime;

	public void setMarketPrice(Float price) {
		this.marketPrice = PriceUtils.toCentPrice(price);
	}

	public void setMarketPrice(Long centPrice) {
		this.marketPrice = centPrice;
	}
}
