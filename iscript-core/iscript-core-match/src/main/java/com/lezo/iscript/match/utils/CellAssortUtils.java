package com.lezo.iscript.match.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellStat;

public class CellAssortUtils {
    private static final Comparator<CellStat> CMP_VALUE_INDEX_ASC = new Comparator<CellStat>() {
        @Override
        public int compare(CellStat o1, CellStat o2) {
            return o1.getValue().getIndex() - o2.getValue().getIndex();
        }
    };

    private static final Comparator<CellStat> CMP_VALUE_LEN_DESC = new Comparator<CellStat>() {
        @Override
        public int compare(CellStat o1, CellStat o2) {
            return o2.getValue().getValue().length() - o1.getValue().getValue().length();
        }
    };
    private static final Comparator<CellStat> CMP_COUNT_DESC = new Comparator<CellStat>() {
        @Override
        public int compare(CellStat o1, CellStat o2) {
            return o2.getCount() - o1.getCount();
        }
    };
    private static final Comparator<CellStat> CMP_LENGTH_DESC = new Comparator<CellStat>() {
        @Override
        public int compare(CellStat o1, CellStat o2) {
            return o2.getLength() - o1.getLength();
        }
    };

    public static void doAnalyse(CellAssort assort) {
        if (assort.getStats().size() == 1) {
            assort.setValue(assort.getStats().get(0));
            return;
        }
        Map<CellStat, Integer> statValMap = Maps.newHashMap();
        List<CellStat> statList = assort.getStats();
        Collections.sort(statList, CMP_VALUE_INDEX_ASC);
        addCount(statValMap, statList.get(0));
        Collections.sort(statList, CMP_VALUE_LEN_DESC);
        addCount(statValMap, statList.get(0));
        Collections.sort(statList, CMP_COUNT_DESC);
        addCount(statValMap, statList.get(0));
        Collections.sort(statList, CMP_LENGTH_DESC);
        addCount(statValMap, statList.get(0));

        CellStat value = null;
        int max = 0;
        for (Entry<CellStat, Integer> entry : statValMap.entrySet()) {
            if (max < entry.getValue()) {
                value = entry.getKey();
                max = entry.getValue();
            }
        }
        assort.setValue(value);

    }

    private static void addCount(Map<CellStat, Integer> statValMap, CellStat cellStat) {
        Integer value = statValMap.get(cellStat);
        if (value == null) {
            value = 1;
        } else {
            value++;
        }
        statValMap.put(cellStat, value);
    }
}
