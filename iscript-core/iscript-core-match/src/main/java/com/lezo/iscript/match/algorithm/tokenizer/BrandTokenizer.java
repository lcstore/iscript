package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.map.BrandMapper;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellTokenUtils;

/**
 * 单位前是字母，目前无法切出单位(通用的单位切取，暂不考虑这种情况)
 * 
 * @author lezo
 * @since 2015年10月11日
 */
public class BrandTokenizer implements ITokenizer {
    private static final Pattern NUM_WORD_WHOLE_REG = Pattern.compile("^[0-9a-zA-Z]+$");
    private static final int STABLE_COUNT = 2;

    @Override
    public List<CellToken> token(String origin) {
        if (StringUtils.isBlank(origin)) {
            return Collections.emptyList();
        }
        // TODO 中文符号转换成英文符号julie‘s--julie's
        Set<CellToken> newCellSet = Sets.newHashSet();
        String sValue = origin;
        BrandMapper mapper = BrandMapper.getInstance();
        CellToken originToken = new CellToken();
        originToken.setIndex(0);
        originToken.setOrigin(origin);

        for (int i = 0; i < sValue.length(); i++) {
            Character firstChar = sValue.charAt(i);
            Set<SameEntity> sameSet = mapper.getEntitySet(firstChar);
            if (sameSet == null) {
                continue;
            }
            for (SameEntity ss : sameSet) {
                for (String sToken : ss.getSameSet()) {
                    int offset = 0;
                    boolean isWord = isWord(sToken);
                    while (true) {
                        int index = sValue.indexOf(sToken, offset);
                        if (index < 0) {
                            break;
                        }
                        if (isWord) {
                            int lIndex = index - 1;
                            int rIndex = index + sToken.length();
                            String lChar = lIndex < 0 ? null : String.valueOf(sValue.charAt(lIndex));
                            String rChar = rIndex >= sValue.length() ? null : String.valueOf(sValue.charAt(rIndex));
                            if (!isWord(lChar) && !isWord(rChar)) {
                                addNewCell(sToken, index, originToken, newCellSet);
                            }
                        } else {
                            addNewCell(sToken, index, originToken, newCellSet);
                        }
                        offset += index + sToken.length();
                    }
                }
            }
        }
        decideStable(newCellSet);
        return Lists.newArrayList(newCellSet);
    }

    private void decideStable(Set<CellToken> newCellSet) {
        if (newCellSet.size() < STABLE_COUNT) {
            return;
        }
        Map<SameEntity, List<CellToken>> same2CellsMap = Maps.newHashMap();
        BrandMapper mapper = BrandMapper.getInstance();
        // TODO 永辉=macau wingfai, 澳门永辉,macau wingfai
        for (CellToken cell : newCellSet) {
            SameEntity same = mapper.getSameEntity(cell.getValue());
            List<CellToken> cellList = same2CellsMap.get(same);
            if (cellList == null) {
                cellList = Lists.newArrayList();
                same2CellsMap.put(same, cellList);
            }
            cellList.add(cell);
        }
        for (Entry<SameEntity, List<CellToken>> entry : same2CellsMap.entrySet()) {
            if (entry.getValue().size() < STABLE_COUNT) {
                continue;
            }
            for (CellToken cell : entry.getValue()) {
                cell.setStable(true);
            }
        }
    }

    private boolean isWord(String sContain) {
        if (StringUtils.isBlank(sContain)) {
            return false;
        }
        return NUM_WORD_WHOLE_REG.matcher(sContain).find();
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

}
