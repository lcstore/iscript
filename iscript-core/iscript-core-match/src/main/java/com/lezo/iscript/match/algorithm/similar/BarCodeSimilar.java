package com.lezo.iscript.match.algorithm.similar;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.algorithm.ISimilar;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.utils.CellAssortUtils;

@Getter
@Setter
public class BarCodeSimilar implements ISimilar {
    private int max = 100;
    private int avg = 10;
    private int min = -100;

    @Override
    public int similar(CellAssort current, CellAssort refer) {
        String curValue = CellAssortUtils.toValue(current);
        String referValue = CellAssortUtils.toValue(refer);
        if (StringUtils.isBlank(curValue) || StringUtils.isBlank(referValue)) {
            return avg;
        }
        return referValue.equals(curValue) ? max : min;
    }

}
