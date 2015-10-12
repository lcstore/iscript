package com.lezo.iscript.match.algorithm.analyse;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;

public class BrandAnalyser implements IAnalyser {

    @Override
    public CellAssort analyse(List<CellToken> tokens) {
        CellAssort assort = new CellAssort();
        assort.setName(NAME_BRAND);
        if (CollectionUtils.isEmpty(tokens)) {
            return assort;
        }
        return assort;
    }

}
