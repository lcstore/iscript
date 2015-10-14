package com.lezo.iscript.match.algorithm.analyse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.map.BrandMapper;
import com.lezo.iscript.match.map.SameChars;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellStat;
import com.lezo.iscript.match.pojo.CellToken;

@Log4j
public class BrandAnalyser implements IAnalyser {
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

    @Override
    public CellAssort analyse(List<CellToken> tokens) {
        CellAssort assort = new CellAssort();
        assort.setName(NAME_BRAND);
        if (CollectionUtils.isEmpty(tokens)) {
            return assort;
        }
        BrandMapper mapper = BrandMapper.getInstance();
        Map<SameChars, CellStat> cellStatMap = Maps.newHashMap();
        for (CellToken cell : tokens) {
            SameChars sameSet = mapper.getSameSet(cell.getValue());
            if (sameSet == null) {
                continue;
            }
            CellStat cellStat = cellStatMap.get(sameSet);
            if (cellStat == null) {
                cellStat = new CellStat();
                List<CellToken> sameCells = Lists.newArrayList();
                cellStat.setTokens(sameCells);
                cellStatMap.put(sameSet, cellStat);
            }
            cellStat.getTokens().add(cell);
        }
        for (CellStat cellStat : cellStatMap.values()) {
            doStatistic(cellStat);
        }
        if (!cellStatMap.isEmpty()) {
            List<CellStat> stats = Lists.newArrayList(cellStatMap.values());
            assort.setStats(stats);
            doAnalyse(assort);
        }
        return assort;
    }

    private void doAnalyse(CellAssort assort) {
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

    private void addCount(Map<CellStat, Integer> statValMap, CellStat cellStat) {
        Integer value = statValMap.get(cellStat);
        if (value == null) {
            value = 1;
        } else {
            value++;
        }
        statValMap.put(cellStat, value);
    }

    private void doStatistic(CellStat cellStat) {
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
