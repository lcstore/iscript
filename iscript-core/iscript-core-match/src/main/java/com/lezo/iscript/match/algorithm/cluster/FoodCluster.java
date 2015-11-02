package com.lezo.iscript.match.algorithm.cluster;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.map.BrandMapper;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.map.UnitMapper;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.SimilarFact;
import com.lezo.iscript.match.pojo.SimilarIn;
import com.lezo.iscript.match.pojo.SimilarOut;
import com.lezo.iscript.match.utils.SimilarUtils;

public class FoodCluster extends SimilarCluster {
    private static final int MUST_MATCH = 100;
    private static final int NOT_MATCH = -100;

    @Override
    protected SimilarOut createSimilarOut(SimilarIn current, SimilarIn refer, List<SimilarFact> facts) {
        SimilarOut newOut = new SimilarOut();
        newOut.setCurrent(current);
        newOut.setRefer(refer);
        if (current == refer) {
            newOut.setScore(MUST_MATCH);
            return newOut;
        }
        // 条码匹配判断
        CellAssort cassort = current.getBarCode();
        CellAssort rassort = refer.getBarCode();
        if (cassort != null && rassort != null) {
            String cVal = cassort.getValue().getValue().getValue();
            String rVal = rassort.getValue().getValue().getValue();
            if (StringUtils.isNotBlank(cVal) && StringUtils.isNotBlank(rVal)) {
                if (cVal.equals(rVal)) {
                    newOut.setScore(MUST_MATCH);
                } else {
                    newOut.setScore(NOT_MATCH);
                }
                return newOut;
            }
        }
        // 品牌匹配判断
        cassort = current.getTokenBrand();
        rassort = refer.getTokenBrand();
        String cBrand = cassort.getValue() == null ? null : cassort.getValue().getValue().getValue();
        String rBrand = rassort.getValue() == null ? null : rassort.getValue().getValue().getValue();
        if (StringUtils.isBlank(cBrand) || StringUtils.isBlank(rBrand)) {
            newOut.setScore(NOT_MATCH);
            return newOut;
        }
        if (!cBrand.equals(rBrand)) {
            SameEntity cEntity = BrandMapper.getInstance().getSameEntity(cBrand);
            SameEntity rEntity = BrandMapper.getInstance().getSameEntity(rBrand);
            if (!cEntity.equals(rEntity)) {
                newOut.setScore(NOT_MATCH);
                return newOut;
            }
        }
        // 单位匹配判断
        cassort = current.getTokenUnit();
        rassort = refer.getTokenUnit();
        String cUnit = cassort.getValue() == null ? null : cassort.getValue().getValue().getValue();
        String rUnit = rassort.getValue() == null ? null : rassort.getValue().getValue().getValue();
        if (StringUtils.isNotBlank(cUnit) && StringUtils.isNotBlank(rUnit)) {
            if (!cUnit.equals(rUnit)) {
                String cUnitKey = cUnit.replaceAll("[0-9.]+", "");
                String rUnitKey = rUnit.replaceAll("[0-9.]+", "");
                UnitMapper unitMapper = UnitMapper.getInstance();
                SameEntity cEntity = unitMapper.getSameEntity(cUnitKey);
                SameEntity rEntity = unitMapper.getSameEntity(rUnitKey);
                // 考虑候选的单位
                if (cEntity != rEntity) {
                    newOut.setScore(NOT_MATCH);
                    return newOut;
                }
                cUnit = cUnit.replace(cUnitKey, rUnitKey);
                if (!cUnit.equals(rUnit)) {
                    newOut.setScore(NOT_MATCH);
                    return newOut;
                }
            }
        }
        // 型号匹配判断
        cassort = current.getTokenModel();
        rassort = refer.getTokenModel();
        String cModel = cassort.getValue() == null ? null : cassort.getValue().getValue().getValue();
        String rModel = rassort.getValue() == null ? null : rassort.getValue().getValue().getValue();
        if (StringUtils.isNotBlank(cModel) && StringUtils.isNotBlank(rModel)) {
            if (!cModel.equals(rModel)) {
                newOut.setScore(NOT_MATCH);
                return newOut;
            }
        }
        int score = 88;
        if (current.getRemain() == null || refer.getRemain() == null) {
            newOut.setScore(score);
            return newOut;
        }
        String curValue = current.getRemain().getValue().getValue().getValue();
        String referValue = current.getRemain().getValue().getValue().getValue();
        int dist = StringUtils.getLevenshteinDistance(curValue, referValue);
        int max = Math.max(curValue.length(), referValue.length());
        if (max == 0) {
            score = MUST_MATCH;
        } else {
            score = score + (max * (max - dist) / max);
        }
        newOut.setScore(SimilarUtils.clamp(score));
        return newOut;
    }

}
