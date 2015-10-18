package com.lezo.iscript.match.algorithm.analyse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
import com.lezo.iscript.match.utils.CellTokenUtils;

@Log4j
public class BrandAnalyser implements IAnalyser {
    private static final Pattern NUM_WORD_WHOLE_REG = Pattern.compile("^[0-9a-zA-Z]+$");
    @Override
    public CellAssort analyse(List<CellToken> tokens) {
        CellAssort assort = new CellAssort();
        assort.setName(this.getClass().getSimpleName());
        if (CollectionUtils.isEmpty(tokens)) {
            return assort;
        }
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
        for (CellToken cell : tokens) {
            SameEntity sameChars = mapper.getSameEntity(cell.getValue());
            if (sameChars == null) {
                continue;
            }
            CellStat cellStat = cellStatMap.get(sameChars);
            if (cellStat == null) {
                cellStat = new CellStat();
                List<CellToken> sameCells = Lists.newArrayList();
                cellStat.setTokens(sameCells);
                cellStatMap.put(sameChars, cellStat);
            }
            cellStat.getTokens().add(cell);
        }
        return cellStatMap;
    }

    private void addNewCell(String token, int offset, CellToken largeCell, Set<CellToken> newCellSet) {
        if (!CellTokenUtils.isCellToken(token)) {
            return;
        }
        CellToken containCell = new CellToken();
        containCell.setValue(token);
        containCell.setIndex(largeCell.getIndex() + offset);
        containCell.setCreator(this.getClass().getSimpleName());
        containCell.setOrigin(largeCell.getOrigin());
        newCellSet.add(containCell);
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

    private boolean isWord(String sContain) {
        if (StringUtils.isBlank(sContain)) {
            return false;
        }
        return NUM_WORD_WHOLE_REG.matcher(sContain).find();
    }

}
