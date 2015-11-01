package com.lezo.iscript.match.algorithm.cluster;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.map.BrandMapper;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.SimilarFact;
import com.lezo.iscript.match.pojo.SimilarIn;
import com.lezo.iscript.match.pojo.SimilarOut;

public class FoodCluster extends SimilarCluster {
    private static final int MUST_MATCH = 100;
    private static final int NOT_MATCH = -100;

    @Override
    protected int getSimilarScore(SimilarIn current, SimilarIn refer, List<SimilarFact> facts) {
        if (current == refer) {
            return MUST_MATCH;
        }
        CellAssort cassort = current.getBarCode();
        CellAssort rassort = refer.getBarCode();
        if (cassort != null && rassort != null) {
            String cVal = cassort.getValue().getValue().getValue();
            String rVal = rassort.getValue().getValue().getValue();
            if (StringUtils.isNotBlank(cVal) && StringUtils.isNotBlank(rVal)) {
                if (cVal.equals(rVal)) {
                    return MUST_MATCH;
                } else {
                    return NOT_MATCH;
                }
            }
        }
        cassort = current.getTokenBrand();
        rassort = refer.getTokenBrand();
        String cBrand = cassort.getValue().getValue().getValue();
        String rBrand = rassort.getValue().getValue().getValue();
        if (StringUtils.isBlank(cBrand) || StringUtils.isBlank(rBrand)) {
            return NOT_MATCH;
        }
        if (!cBrand.equals(rBrand)) {
            SameEntity cEntity = BrandMapper.getInstance().getSameEntity(cBrand);
            SameEntity rEntity = BrandMapper.getInstance().getSameEntity(rBrand);
            if (!cEntity.equals(rEntity)) {
                return NOT_MATCH;
            }
        }
        cassort = current.getTokenUnit();
        rassort = refer.getTokenUnit();
        String cUnit = cassort.getValue() == null ? null : cassort.getValue().getValue().getValue();
        String rUnit = rassort.getValue() == null ? null : rassort.getValue().getValue().getValue();
        if (StringUtils.isNotBlank(cUnit) || StringUtils.isNotBlank(rUnit)) {
            String cUnitKey = cUnit.replaceAll("[0-9.]+", "");
            return NOT_MATCH;
        }
        return super.getSimilarScore(current, refer, facts);
    }

    @Override
    protected SimilarOut createSimilarOut(SimilarIn current, SimilarIn refer, List<SimilarFact> facts) {
        return super.createSimilarOut(current, refer, facts);
    }

}
