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
    public static final Comparator<CellStat> CMP_VALUE_INDEX_ASC = new Comparator<CellStat>() {
        @Override
        public int compare(CellStat o1, CellStat o2) {
            return o1.getValue().getIndex() - o2.getValue().getIndex();
        }
    };

    public static final Comparator<CellStat> CMP_VALUE_LEN_DESC = new Comparator<CellStat>() {
        @Override
        public int compare(CellStat o1, CellStat o2) {
            return o2.getValue().getValue().length() - o1.getValue().getValue().length();
        }
    };
    public static final Comparator<CellStat> CMP_COUNT_DESC = new Comparator<CellStat>() {
        @Override
        public int compare(CellStat o1, CellStat o2) {
            return o2.getCount() - o1.getCount();
        }
    };
    public static final Comparator<CellStat> CMP_LENGTH_DESC = new Comparator<CellStat>() {
        @Override
        public int compare(CellStat o1, CellStat o2) {
            return o2.getLength() - o1.getLength();
        }
    };

    public static void doAnalyse(CellAssort assort, Map<Comparator<CellStat>, Integer> cmpValMap) {
        if (assort.getStats().size() == 1) {
            assort.setValue(assort.getStats().get(0));
            return;
        }
        Map<CellStat, Integer> statValMap = Maps.newHashMap();
        List<CellStat> statList = assort.getStats();
        for (Entry<Comparator<CellStat>, Integer> entry : cmpValMap.entrySet()) {
            Collections.sort(statList, entry.getKey());
            addCount(statValMap, statList.get(0), entry.getValue());
        }
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

    public static void doAnalyse(CellAssort assort) {
        Map<Comparator<CellStat>, Integer> cmpValMap = Maps.newHashMap();
        cmpValMap.put(CMP_VALUE_INDEX_ASC, 8);
        cmpValMap.put(CMP_VALUE_LEN_DESC, 5);
        cmpValMap.put(CMP_COUNT_DESC, 3);
        cmpValMap.put(CMP_LENGTH_DESC, 2);
        doAnalyse(assort, cmpValMap);
    }

    private static void addCount(Map<CellStat, Integer> statValMap, CellStat cellStat, int val) {
        Integer value = statValMap.get(cellStat);
        if (value == null) {
            value = val;
        } else {
            value += val;
        }
        statValMap.put(cellStat, value);
    }
}
