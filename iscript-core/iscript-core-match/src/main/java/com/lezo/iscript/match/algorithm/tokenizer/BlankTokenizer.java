package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.pojo.CellToken;

public class BlankTokenizer implements ITokenizer {
    private static final Pattern BLANK_REG = Pattern.compile("[\\s]+");

    @Override
    public List<CellToken> token(String origin) {
        if (StringUtils.isBlank(origin)) {
            return Collections.emptyList();
        }
        Set<CellToken> cellSet = Sets.newHashSet();
        String[] valArr = BLANK_REG.split(origin);
        int offset = 0;
        for (String value : valArr) {
            if (StringUtils.isBlank(value)) {
                continue;
            }
            CellToken token = new CellToken();
            token.setCreator(this.getClass().getSimpleName());
            token.setOrigin(origin);
            token.setValue(value);
            token.setIndex(token.getOrigin().indexOf(token.getValue(), offset));
            cellSet.add(token);
            offset += value.length();
        }
        return Lists.newArrayList(cellSet);
    }

}
