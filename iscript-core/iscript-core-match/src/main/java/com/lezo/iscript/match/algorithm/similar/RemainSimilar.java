package com.lezo.iscript.match.algorithm.similar;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.algorithm.ISimilar;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.utils.CellAssortUtils;

@Getter
@Setter
public class RemainSimilar implements ISimilar {
    private int max = 100;
    private int min = 50;

    @Override
    public int similar(CellAssort current, CellAssort refer) {
        String curValue = CellAssortUtils.toValue(current);
        String referValue = CellAssortUtils.toValue(refer);
        if (StringUtils.isBlank(curValue) || StringUtils.isBlank(referValue)) {
            return min;
        }
        int dist = StringUtils.getLevenshteinDistance(curValue, referValue);
        int max = Math.max(curValue.length(), referValue.length());
        return (max * (max - dist) / max);
    }

}
