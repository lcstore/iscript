package com.lezo.iscript.match.algorithm.strainer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.IStrainer;
import com.lezo.iscript.match.algorithm.utils.CellTokenUtils;
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
    private static final Pattern NUM_WORD_WHOLE_REG = Pattern.compile("^[0-9a-zA-Z]+$");
    private static final Comparator<CellToken> CMP_LEN_ASC = new Comparator<CellToken>() {
        @Override
        public int compare(CellToken o1, CellToken o2) {
            return o1.getValue().length() - o2.getValue().length();
        }
    };

    @Override
    public List<CellToken> strain(List<CellToken> oldCells) {
        if (CollectionUtils.isEmpty(oldCells)) {
            return oldCells;
        }
        Set<CellToken> newCellSet = Sets.newHashSet();
        while (splitContains(oldCells, newCellSet)) {
            oldCells = Lists.newArrayList(newCellSet);
            newCellSet.clear();
        }
        return oldCells;
    }

    private boolean splitContains(List<CellToken> oldCells, Set<CellToken> newCellSet) {
        Collections.sort(oldCells, CMP_LEN_ASC);
        Set<Integer> splitSet = Sets.newHashSet();
        int oldSize = oldCells.size();
        for (int i = 0; i < oldSize; i++) {
            if (splitSet.contains(i)) {
                continue;
            }
            CellToken smallCell = oldCells.get(i);
            if (StringUtils.isBlank(smallCell.getValue())) {
                continue;
            }
            for (int j = oldSize - 1; j > i; j--) {
                if (splitSet.contains(j)) {
                    continue;
                }
                CellToken largeCell = oldCells.get(j);
                String sValue = largeCell.getValue();
                if (sValue.length() == smallCell.getValue().length()) {
                    continue;
                }
                int offset = 0;
                String sContain = smallCell.getValue();
                boolean isWord = isWord(sContain);
                int oldCount = newCellSet.size();
                while (true) {
                    int index = sValue.indexOf(sContain, offset);
                    if (index < 0) {
                        if (newCellSet.size() > oldCount && offset < sValue.length()) {
                            String rightChars = sValue.substring(offset);
                            addNewCell(rightChars, offset, largeCell, newCellSet);
                        }
                        break;
                    }
                    if (isWord) {
                        int lIndex = index - 1;
                        int rIndex = index + sContain.length();
                        String lChar = lIndex < 0 ? null : String.valueOf(sValue.charAt(lIndex));
                        String rChar = rIndex >= sValue.length() ? null : String.valueOf(sValue.charAt(rIndex));
                        if (!isWord(lChar) && !isWord(rChar)) {
                            String leftChars = sValue.substring(offset, index);
                            addNewCell(leftChars, offset, largeCell, newCellSet);
                            addNewCell(smallCell.getValue(), index, largeCell, newCellSet);
                        }
                    } else {
                        String leftChars = sValue.substring(offset, index);
                        addNewCell(leftChars, offset, largeCell, newCellSet);
                        addNewCell(smallCell.getValue(), index, largeCell, newCellSet);
                    }
                    offset += index + sContain.length();

                }
                if (oldCount < newCellSet.size()) {
                    splitSet.add(j);
                }

            }
        }
        boolean hasNew = !newCellSet.isEmpty();
        // remove split
        for (int i = 0; i < oldCells.size(); i++) {
            if (splitSet.contains(i)) {
                continue;
            }
            newCellSet.add(oldCells.get(i));
        }
        return hasNew;
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

    private boolean isWord(String sContain) {
        if (StringUtils.isBlank(sContain)) {
            return false;
        }
        return NUM_WORD_WHOLE_REG.matcher(sContain).find();
    }
}
