package com.lezo.iscript.match.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarIn {
    private String skuCode;
    private String productName;
    private CellAssort wareCode;
    private CellAssort barCode;
    // private CellAssort marketPrice;
    // private CellAssort tokenCategory;
    private CellAssort tokenBrand;
    private CellAssort tokenModel;
    private CellAssort tokenUnit;
    private CellAssort remain;
    private Integer arbiterId;

    @Override
    public String toString() {
        return "SimilarIn [productName=" + productName + "]";
    }


}
