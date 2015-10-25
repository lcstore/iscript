package com.lezo.iscript.match.algorithm.similar;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.algorithm.ISimilar;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.map.UnitMapper;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellStat;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellAssortUtils;

@Getter
@Setter
public class UnitSimilar implements ISimilar {
    private int max = 100;
    private int avg = 50;
    private int min = 0;

    @Override
    public int similar(CellAssort current, CellAssort refer) {
        String curValue = CellAssortUtils.toValue(current);
        String referValue = CellAssortUtils.toValue(refer);
        if (StringUtils.isBlank(curValue) || StringUtils.isBlank(referValue)) {
            return avg;
        }
        boolean isSame = isSameToken(current.getValue().getValue(), refer.getValue().getValue());
        if (!isSame) {
            return min;
        }
        int count = 0;
        for (CellStat stat : current.getStats()) {
            for (CellStat rstat : refer.getStats()) {
                if (isSameToken(stat.getValue(), rstat.getValue())) {
                    count++;
                    break;
                }
            }
        }
        int score = 0;
        if (isSameToken(current.getValue().getValue(), refer.getValue().getValue())) {
            score = 80;
        }
        score += (count * max / current.getStats().size());
        return score;
    }

    private boolean isSameToken(CellToken current, CellToken refer) {
        if (current == null || refer == null) {
            return false;
        }
        String curValue = current.getValue();
        String referValue = refer.getValue();
        if (referValue.equals(curValue)) {
            return true;
        }
        String sReferUnit = referValue.replaceAll("[0-9.]+", "");
        String sCurUnit = curValue.replaceAll("[0-9.]+", "");
        SameEntity referEntity = UnitMapper.getInstance().getSameEntity(sReferUnit);
        SameEntity currentEntity = UnitMapper.getInstance().getSameEntity(sCurUnit);
        if (referEntity == null || currentEntity == null) {
            return false;
        }
        String newRefer = referValue.replace(sReferUnit, referEntity.getValue());
        String newCur = curValue.replace(sCurUnit, currentEntity.getValue());
        return newRefer.equals(newCur);
    }

}
