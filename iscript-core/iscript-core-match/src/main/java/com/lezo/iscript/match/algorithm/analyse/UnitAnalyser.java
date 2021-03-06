package com.lezo.iscript.match.algorithm.analyse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.map.UnitMapper;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellStat;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellAssortUtils;
import com.lezo.iscript.match.utils.CellStatUtils;

public class UnitAnalyser implements IAnalyser {
    private static final Pattern UNIT_REG = Pattern.compile("^([0-9.]+)([a-zA-Z\u4E00-\u9FA5]+)$");
    private static final Map<Comparator<CellStat>, Integer> CMP_VAL_MAP = Maps.newHashMap();
    static {
        CMP_VAL_MAP.put(CellAssortUtils.CMP_VALUE_LEN_DESC, 5);
        CMP_VAL_MAP.put(CellAssortUtils.CMP_COUNT_DESC, 3);
        CMP_VAL_MAP.put(CellAssortUtils.CMP_LENGTH_DESC, 2);
    }

    @Override
    public CellAssort analyse(List<CellToken> tokens) {
        CellAssort assort = new CellAssort();
        assort.setName(this.getClass().getSimpleName());
        if (CollectionUtils.isEmpty(tokens)) {
            return assort;
        }
        UnitMapper mapper = UnitMapper.getInstance();
        Map<SameEntity, CellStat> cellStatMap = Maps.newHashMap();
        for (CellToken token : tokens) {
            Matcher matcher = UNIT_REG.matcher(token.getValue());
            if (!matcher.find()) {
                continue;
            }
            String sUnitChar = matcher.group(2);
            SameEntity sameChars = mapper.getSameEntity(sUnitChar);
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
            cellStat.getTokens().add(token);
        }
        for (CellStat cellStat : cellStatMap.values()) {
            CellStatUtils.doStatistic(cellStat);
        }
        if (!cellStatMap.isEmpty()) {
            List<CellStat> stats = Lists.newArrayList(cellStatMap.values());
            assort.setStats(stats);
            CellAssortUtils.doAnalyse(assort, CMP_VAL_MAP);
        }
        return assort;
    }

}
