package com.lezo.iscript.similar;

import java.util.Set;

import lombok.Data;

@Data
public class SimilarParam {
    public static final Integer TYPE_PRODUCT_CODE = 0;
    public static final Integer TYPE_SKU_CODE = 1;
    public static final Integer TYPE_SHOP_ID = 2;
    private Integer siteId;
    private Integer idType = TYPE_PRODUCT_CODE;
    private Set<String> idSet;
    private Set<Integer> siteSet;
    private Boolean flushGlobal = false;
}
