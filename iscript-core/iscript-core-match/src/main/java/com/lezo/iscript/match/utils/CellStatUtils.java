package com.lezo.iscript.match.utils;

import java.util.Set;

import com.google.common.collect.Sets;
import com.lezo.iscript.match.pojo.CellStat;
import com.lezo.iscript.match.pojo.CellToken;

public class CellStatUtils {

    public static void doStatistic(CellStat cellStat) {
        if (cellStat == null || cellStat.getTokens() == null) {
            return;
        }
        CellToken headToken = null;
        int len = 0;
        Set<String> tokenSet = Sets.newHashSet();
        for (CellToken token : cellStat.getTokens()) {
            if (headToken == null || headToken.getIndex() > token.getIndex()) {
                headToken = token;
            }
            tokenSet.add(token.getValue());
            len += token.getValue().length();
        }
        cellStat.setValue(headToken);
        cellStat.setLength(len);
        cellStat.setCount(tokenSet.size());
    }
}
