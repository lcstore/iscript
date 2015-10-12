package com.lezo.iscript.match.algorithm.strainer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Lists;
import com.lezo.iscript.match.algorithm.IStrainer;
import com.lezo.iscript.match.pojo.CellToken;

/**
 * 【美孚10w40】美孚（Mobil） 美孚速霸1000机油 10w40 SM级 （4L装） 【行情 报价 价格 评测】
 * 
 * 
 * @author lezo
 * @since 2015年10月11日
 */
public class SuffixStrainer implements IStrainer {
    private static final Pattern BRACKET_END_REG = Pattern
            .compile("(?<=【)[^】]{1,}?(?=】[\\s]*?$)|(?<=\\[)[^\\]]{1,}?(?=\\][\\s]*?$)");

    @Override
    public List<CellToken> strain(List<CellToken> tokens) {
        if (CollectionUtils.isEmpty(tokens)) {
            return tokens;
        }
        String origin = tokens.get(0).getOrigin();
        Matcher matcher = BRACKET_END_REG.matcher(origin);
        List<CellToken> dataList = Lists.newArrayList();
        while (matcher.find()) {
            int start = matcher.start() - 1;
            for (CellToken token : tokens) {
                if (token.getIndex() >= start) {
                    continue;
                }
                dataList.add(token);
            }
        }
        return dataList.isEmpty() ? tokens : dataList;
    }

}
