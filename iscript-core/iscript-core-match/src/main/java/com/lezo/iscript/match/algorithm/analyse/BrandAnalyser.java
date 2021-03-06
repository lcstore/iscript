package com.lezo.iscript.match.algorithm.analyse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.map.BrandMapper;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellStat;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellAssortUtils;

public class BrandAnalyser implements IAnalyser {
    private static final Comparator<CellToken> CMP_LEN_DESC = new Comparator<CellToken>() {
        @Override
        public int compare(CellToken o1, CellToken o2) {
            return o2.getValue().length() - o1.getValue().length();
        }
    };

    @Override
    public CellAssort analyse(List<CellToken> tokens) {
        CellAssort assort = new CellAssort();
        assort.setName(this.getClass().getSimpleName());
        if (CollectionUtils.isEmpty(tokens)) {
            return assort;
        }
        // tokens = TOKENIZER.token(tokens.get(0).getOrigin());
        Map<SameEntity, CellStat> cellStatMap = toCellStatMap(tokens);
        for (CellStat cellStat : cellStatMap.values()) {
            doStatistic(cellStat);
        }
        if (!cellStatMap.isEmpty()) {
            List<CellStat> stats = Lists.newArrayList(cellStatMap.values());
            assort.setStats(stats);
            CellAssortUtils.doAnalyse(assort);
        }
        return assort;
    }

    private Map<SameEntity, CellStat> toCellStatMap(List<CellToken> tokens) {
        if (CollectionUtils.isEmpty(tokens)) {
            return Collections.emptyMap();
        }
        BrandMapper mapper = BrandMapper.getInstance();
        Map<SameEntity, CellStat> cellStatMap = Maps.newHashMap();

        // 移除被包含的品牌
        Set<CellToken> delCellSet = Sets.newHashSet();
        for (int i = 0; i < tokens.size() - 2; i++) {
            CellToken smallCell = tokens.get(i);
            for (int j = tokens.size() - 1; j > i; j--) {
                CellToken largeCell = tokens.get(j);
                if (largeCell.getValue().contains(smallCell.getValue())) {
                    // 移除短字符串的品牌
                    delCellSet.add(smallCell);
                    break;
                }
            }
        }
        for (CellToken cell : tokens) {
            if (delCellSet.contains(cell)) {
                continue;
            }
            SameEntity sEntity = mapper.getSameEntity(cell.getValue());
            if (sEntity == null) {
                continue;
            }
            CellStat cellStat = cellStatMap.get(sEntity);
            // 同义词合并 （魅纪, meiji ==明治,meiji）
            cellStat = cellStat == null ? getSameCellStat(cellStatMap, sEntity) : cellStat;
            if (cellStat == null) {
                cellStat = new CellStat();
                List<CellToken> sameCells = Lists.newArrayList();
                cellStat.setTokens(sameCells);
                cellStatMap.put(sEntity, cellStat);
            }
            if (cell.getValue().equals(sEntity.getValue())) {
                cellStat.setValue(cell);
            }
            cellStat.getTokens().add(cell);
        }
        for (Entry<SameEntity, CellStat> entry : cellStatMap.entrySet()) {
            if (entry.getValue().getValue() != null) {
                continue;
            }
            List<CellToken> tks = entry.getValue().getTokens();
            Collections.sort(tks, CMP_LEN_DESC);
            entry.getValue().setValue(tks.get(0));
        }
        return cellStatMap;
    }

    private CellStat getSameCellStat(Map<SameEntity, CellStat> cellStatMap, SameEntity sEntity) {
        for (Entry<SameEntity, CellStat> scEntry : cellStatMap.entrySet()) {
            Set<String> referSet = scEntry.getKey().getSameSet();
            for (String sVal : sEntity.getSameSet()) {
                if (referSet.contains(sVal)) {
                    return scEntry.getValue();
                }
            }
        }
        return null;
    }

    private void doStatistic(CellStat cellStat) {
        if (cellStat == null || cellStat.getTokens() == null) {
            return;
        }
        int len = 0;
        Set<String> tokenSet = Sets.newHashSet();
        for (CellToken token : cellStat.getTokens()) {
            tokenSet.add(token.getValue());
            len += token.getValue().length();
        }
        cellStat.setLength(len);
        cellStat.setCount(tokenSet.size());
    }

}
