package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.pojo.CellToken;

/**
 * 括号内切词
 * 
 * @author lezo
 * @since 2015年10月11日
 */
public class BracketTokenizer implements ITokenizer {
    private static final Pattern BRACKET_REG = Pattern
            .compile("(?<=（).{1,}?(?=）)|(?<=\\().{1,}?(?=\\))|(?<=【).{1,}?(?=】)|(?<=\\[).{1,}?(?=\\])");

    @Override
    public List<CellToken> token(String origin) {
        if (StringUtils.isBlank(origin)) {
            return Collections.emptyList();
        }
        Set<CellToken> cellSet = Sets.newHashSet();
        Matcher matcher = BRACKET_REG.matcher(origin);
        while (matcher.find()) {
            String value = matcher.group();
            if (StringUtils.isBlank(value)) {
                continue;
            }
            CellToken token = new CellToken();
            token.setCreator(this.getClass().getSimpleName());
            token.setOrigin(origin);
            token.setValue(value);
            token.setIndex(token.getOrigin().indexOf(token.getValue()));
            cellSet.add(token);
        }
        return Lists.newArrayList(cellSet);
    }

}
