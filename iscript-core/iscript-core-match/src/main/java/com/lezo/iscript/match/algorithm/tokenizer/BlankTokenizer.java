package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.pojo.CellToken;

public class BlankTokenizer implements ITokenizer {
    private static final Pattern BLANK_REG = Pattern.compile("[\\s]+");

    @Override
    public List<CellToken> token(String origin) {
        if (StringUtils.isBlank(origin)) {
            return Collections.emptyList();
        }
        String[] valArr = BLANK_REG.split(origin);
        List<CellToken> tokens = new ArrayList<CellToken>(valArr.length);
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
            tokens.add(token);
            offset += value.length();
        }
        return tokens;
    }

}
