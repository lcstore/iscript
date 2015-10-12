package com.lezo.iscript.match.algorithm.strainer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.IStrainer;
import com.lezo.iscript.match.pojo.CellToken;

/**
 * 1.【美孚10w40】美孚（Mobil） 美孚速霸1000机油 10w40 SM级 （4L装） 【行情 报价 价格 评测】
 * 
 * 2.【美孚10w40】美孚,10w40
 * 
 * 3.【美孚,10w40,】美孚,10w40
 * 
 * @author lezo
 * @since 2015年10月11日
 */
public class ContainStrainer implements IStrainer {
    private static final Pattern SIGN_REG = Pattern.compile("^[【】（）\\s]+$");
    private static final Pattern NUM_WORD_WHOLE_REG = Pattern.compile("^[0-9a-zA-Z]+$");
    private static final Pattern NUM_WORD_END_REG = Pattern.compile("[0-9a-zA-Z]+$");
    private static final Pattern NUM_WORD_START_REG = Pattern.compile("^[0-9a-zA-Z]+");
    private static final Comparator<CellToken> CMP_LEN_ASC = new Comparator<CellToken>() {
        @Override
        public int compare(CellToken o1, CellToken o2) {
            return o1.getToken().length() - o2.getToken().length();
        }
    };

    @Override
    public List<CellToken> strain(List<CellToken> oldCells) {
        if (CollectionUtils.isEmpty(oldCells)) {
            return oldCells;
        }
        List<CellToken> newCells = Lists.newArrayList();
        while (splitContains(oldCells, newCells)) {
            oldCells = newCells;
            newCells = Lists.newArrayList();
            System.err.println("---------------------");
        }
        return newCells;
    }

    private boolean splitContains(List<CellToken> oldCells, List<CellToken> newCells) {
        Collections.sort(oldCells, CMP_LEN_ASC);
        Set<Integer> splitSet = Sets.newHashSet();
        int oldSize = oldCells.size();
        for (int i = 0; i < oldSize; i++) {
            if (splitSet.contains(i)) {
                continue;
            }
            CellToken smallCell = oldCells.get(i);
            if (StringUtils.isBlank(smallCell.getToken())) {
                continue;
            }
            for (int j = oldSize - 1; j > i; j--) {
                if (splitSet.contains(j)) {
                    continue;
                }
                CellToken largeCell = oldCells.get(j);
                String sValue = largeCell.getToken();
                if (sValue.length() == smallCell.getToken().length()) {
                    continue;
                }
                String[] splits = sValue.split(smallCell.getToken());
                if (splits.length > 1) {
                    splitSet.add(j);
                    int offset = 0;
                    for (int k = 0; k < splits.length; k++) {
                        String spValue = splits[k];
                        if (StringUtils.isBlank(spValue)) {
                            continue;
                        }
                        int traceIndex = offset - smallCell.getToken().length();
                        traceIndex = traceIndex < 0 ? 0 : traceIndex;
                        if (canToken(spValue, sValue.substring(traceIndex))) {
                            if (StringUtils.isBlank(spValue)) {
                                continue;
                            }
                            Matcher matcher = SIGN_REG.matcher(spValue);
                            if (matcher.find()) {
                                continue;
                            }
                            CellToken containCell = new CellToken();
                            containCell.setToken(spValue);
                            containCell.setIndex(largeCell.getIndex() + offset);
                            containCell.setCreator(this.getClass().getSimpleName());
                            containCell.setOrigin(largeCell.getOrigin());
                            newCells.add(containCell);
                        }
                        offset += spValue.length();
                        if (k + 1 < splits.length) {
                            offset += smallCell.getToken().length();
                        }
                    }
                }
            }
        }
        System.err.println("newCells:" + newCells.size() + ",newCells:" + ArrayUtils.toString(newCells));
        boolean hasNew = !newCells.isEmpty();
        // remove split
        for (int i = 0; i < oldCells.size(); i++) {
            if (splitSet.contains(i)) {
                continue;
            }
            newCells.add(oldCells.get(i));
        }
        return hasNew;
    }

    private boolean canToken(String token, String origin) {
        Matcher matcher = NUM_WORD_WHOLE_REG.matcher(token);
        if (matcher.find()) {
            String[] splits = origin.split(token);
            if (splits.length >= 2) {
                int index = -1;
                // 分割的词为字母数字，左右两边不能为字母数字
                Matcher lMatcher = NUM_WORD_END_REG.matcher(splits[++index]);
                Matcher rMatcher = NUM_WORD_START_REG.matcher(splits[++index]);
                if (lMatcher.find() || rMatcher.find()) {
                    return false;
                }
            }
        }
        return true;
    }
}
