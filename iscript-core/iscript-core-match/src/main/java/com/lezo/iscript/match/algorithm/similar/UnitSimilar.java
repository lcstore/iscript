package com.lezo.iscript.match.algorithm.similar;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.algorithm.ISimilar;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.map.UnitMapper;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.utils.CellAssortUtils;

@Getter
@Setter
public class UnitSimilar implements ISimilar {
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
        if (referValue.equals(curValue)) {
            return max;
        }
        String sReferUnit = referValue.replaceAll("[0-9.]+", "");
        String sCurUnit = curValue.replaceAll("[0-9.]+", "");
        SameEntity referEntity = UnitMapper.getInstance().getSameEntity(sReferUnit);
        SameEntity currentEntity = UnitMapper.getInstance().getSameEntity(sCurUnit);
        if (referEntity == null || currentEntity == null) {
            return min;
        }
        String newRefer = referValue.replace(sReferUnit, referEntity.getValue());
        String newCur = curValue.replace(sCurUnit, currentEntity.getValue());
        return newRefer.equals(newCur) ? max : min;
    }

}
