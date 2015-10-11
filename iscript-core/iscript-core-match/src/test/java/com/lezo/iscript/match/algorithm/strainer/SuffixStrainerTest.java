package com.lezo.iscript.match.algorithm.strainer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BlankTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BracketTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.UnitTokenizer;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;

public class SuffixStrainerTest {
    SuffixStrainer strainer = new SuffixStrainer();

    @Test
    public void testSuffixStrainer() {
        String origin = "【良兴LX-561】良兴 LX-561 中英文双语儿童早教机 120页彩色学习卡片 兰色 【行情 报价 价格 评测】";
        System.out.println("origin:" + origin);
        List<ITokenizer> tokenizers = Lists.newArrayList(new BlankTokenizer(),
                new BracketTokenizer(), new UnitTokenizer());
        List<CellToken> tokenSumList = Lists.newArrayList();
        for (ITokenizer tokenizer : tokenizers) {
            List<CellToken> tokenList = tokenizer.token(origin);
            if (CollectionUtils.isNotEmpty(tokenList)) {
                tokenSumList.addAll(tokenList);
            }
        }
        System.out.println("origin.size:" + tokenSumList.size());
        System.out.println("origin.result:" + ArrayUtils.toString(tokenSumList));

        CellAssort cellAssort = strainer.strain(tokenSumList);
        System.out.println("strainer.name:" + cellAssort.getName());
        System.out.println("strainer.size:" + cellAssort.getValue());
        System.out.println("strainer.result:" + ArrayUtils.toString(cellAssort.getTokens()));
    }

}
