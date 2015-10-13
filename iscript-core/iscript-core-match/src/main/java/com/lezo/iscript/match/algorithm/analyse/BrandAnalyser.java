package com.lezo.iscript.match.algorithm.analyse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;

public class BrandAnalyser implements IAnalyser {
    private static final Comparator<CellToken> CMP_LEN_ASC = new Comparator<CellToken>() {
        @Override
        public int compare(CellToken o1, CellToken o2) {
            return o1.getToken().length() - o2.getToken().length();
        }
    };

    @Override
    public CellAssort analyse(List<CellToken> tokens) {
        CellAssort assort = new CellAssort();
        assort.setName(NAME_BRAND);
        if (CollectionUtils.isEmpty(tokens)) {
            return assort;
        }
        Collections.sort(tokens, CMP_LEN_ASC);
        for (CellToken token : tokens) {

        }
        return assort;
    }

}
