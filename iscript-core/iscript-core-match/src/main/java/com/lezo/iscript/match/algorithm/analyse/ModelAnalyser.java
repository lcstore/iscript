package com.lezo.iscript.match.algorithm.analyse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellStat;
import com.lezo.iscript.match.pojo.CellToken;

@Log4j
public class ModelAnalyser implements IAnalyser {
    private static final Pattern EN_WORD_REG = Pattern.compile("[0-9a-zA-Z/\\-\\+]+");
    private static final Pattern EN_WORD_WHOLE_REG = Pattern.compile("^[0-9a-zA-Z/\\-\\+]+$");
    private static final Pattern NUM_REG = Pattern.compile("[0-9]+");
    private static final Pattern EN_REG = Pattern.compile("[a-zA-Z]+");
    private static final Pattern EN_NUM_REG = Pattern.compile("[0-9a-zA-Z]+");

    private static final Comparator<Entry<CellStat, Integer>> CMP_COUNT_DESC =
            new Comparator<Map.Entry<CellStat, Integer>>() {

                @Override
                public int compare(Entry<CellStat, Integer> o1, Entry<CellStat, Integer> o2) {
                    return o2.getValue() - o1.getValue();
                }
            };
    private static final int MIN_LEN = 2;
    private static final int MAX_LEN = 100;

    @Override
    public CellAssort analyse(List<CellToken> tokens) {
        CellAssort assort = new CellAssort();
        assort.setName(this.getClass().getSimpleName());
        if (CollectionUtils.isEmpty(tokens)) {
            return assort;
        }
        Map<String, CellStat> cellStatMap = Maps.newHashMap();
        for (CellToken cell : tokens) {
            Matcher matcher = EN_WORD_REG.matcher(cell.getValue());
            while (matcher.find()) {
                String value = matcher.group();
                if (isSkipLen(value)) {
                    continue;
                }
                CellStat cellStat = cellStatMap.get(value);
                if (cellStat == null) {
                    cellStat = new CellStat();
                    CellToken destToken = new CellToken();
                    destToken.setOrigin(cell.getOrigin());
                    destToken.setCreator(this.getClass().getSimpleName());
                    destToken.setValue(value);
                    destToken.setIndex(destToken.getOrigin().indexOf(destToken.getValue()));
                    cellStat.setValue(destToken);
                    cellStatMap.put(value, cellStat);
                }
                cellStat.setCount(cellStat.getCount() + 1);
            }
        }
        if (!cellStatMap.isEmpty()) {
            List<CellStat> stats = Lists.newArrayList(cellStatMap.values());
            assort.setStats(stats);
            Map<CellStat, Integer> statValMap = Maps.newHashMap();
            for (CellStat token : assort.getStats()) {
                Integer count = 0;
                String sVal = token.getValue().getValue();
                Matcher matcher = EN_WORD_WHOLE_REG.matcher(sVal);
                if (matcher.find()) {
                    count += 13;
                }
                if (sVal.contains("\\") || sVal.contains("-")) {
                    count += 2;
                }
                matcher = NUM_REG.matcher(sVal);
                if (matcher.find()) {
                    count += 10;
                }
                matcher = EN_REG.matcher(sVal);
                if (matcher.find()) {
                    count += 10;
                }
                statValMap.put(token, count);
            }
            ArrayList<Entry<CellStat, Integer>> entryList = Lists.newArrayList(statValMap.entrySet());
            Collections.sort(entryList, CMP_COUNT_DESC);
            assort.setValue(entryList.get(0).getKey());
        }
        return assort;
    }

    private boolean isSkipLen(String sVal) {
        if (StringUtils.isBlank(sVal)) {
            return true;
        }
        sVal = sVal.trim();
        return sVal.length() <= MIN_LEN || sVal.length() > MAX_LEN;
    }
}
