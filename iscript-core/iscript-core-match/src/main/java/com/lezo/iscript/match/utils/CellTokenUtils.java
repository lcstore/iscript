package com.lezo.iscript.match.utils;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.IStrainer;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.algorithm.strainer.ContainStrainer;
import com.lezo.iscript.match.algorithm.strainer.SuffixStrainer;
import com.lezo.iscript.match.algorithm.tokenizer.BlankTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BracketTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BrandTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.UnitTokenizer;
import com.lezo.iscript.match.pojo.CellToken;

public class CellTokenUtils {
    private static final Pattern SIGN_REG = Pattern.compile("^[【】（）\\s]+$");
    private static List<ITokenizer> tokenizers = Lists.newArrayList(new BlankTokenizer(),
            new BracketTokenizer(), new UnitTokenizer(), new BrandTokenizer());
    private static List<IStrainer> strainers = Lists.newArrayList(new SuffixStrainer(), new ContainStrainer());

    /**
     * 不是空白，非特殊字符
     * 
     * @param token
     * @return
     */
    public static boolean isCellToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        Matcher matcher = SIGN_REG.matcher(token);
        return !matcher.find();
    }

    public static List<CellToken> getTokens(String origin) {
        Set<CellToken> sumCellSet = Sets.newHashSet();
        for (ITokenizer tokenizer : tokenizers) {
            List<CellToken> tokenList = tokenizer.token(origin);
            if (CollectionUtils.isNotEmpty(tokenList)) {
                sumCellSet.addAll(tokenList);
            }
        }
        List<CellToken> targets = Lists.newArrayList(sumCellSet);
        for (IStrainer strainer : strainers) {
            targets = strainer.strain(targets);
        }
        return targets;
    }
}
