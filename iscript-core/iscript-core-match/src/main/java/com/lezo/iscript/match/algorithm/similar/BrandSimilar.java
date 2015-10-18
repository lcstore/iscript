package com.lezo.iscript.match.algorithm.similar;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.algorithm.ISimilar;
import com.lezo.iscript.match.map.BrandMapper;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.utils.CellAssortUtils;

@Getter
@Setter
public class BrandSimilar implements ISimilar {
    private int max = 100;
    private int min = -100;

    @Override
    public int similar(CellAssort current, CellAssort refer) {
        String curValue = CellAssortUtils.toValue(current);
        String referValue = CellAssortUtils.toValue(refer);
        if (StringUtils.isBlank(referValue)) {
            return min;
        }
        if (referValue.equals(curValue)) {
            return max;
        }
        SameEntity referEntity = BrandMapper.getInstance().getSameEntity(referValue);
        SameEntity currentEntity = BrandMapper.getInstance().getSameEntity(curValue);
        return referEntity == currentEntity ? max : min;
    }

}
