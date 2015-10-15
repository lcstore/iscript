package com.lezo.iscript.match.algorithm.analyse;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.lezo.iscript.match.algorithm.IStrainer;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.algorithm.strainer.ContainStrainer;
import com.lezo.iscript.match.algorithm.strainer.SuffixStrainer;
import com.lezo.iscript.match.algorithm.tokenizer.BlankTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BracketTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.UnitTokenizer;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;

public class ModelAnalyserTest {
    ModelAnalyser analyser = new ModelAnalyser();

    @Test
    public void testAnalyser() {
        String origin = "【良兴LX-561】良兴 LX-561 中英文双语儿童早教机 120页彩色学习卡片 兰色 【行情 报价 价格 评测】";
        List<CellToken> tokens = getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("lx-561", assort.getValue().getValue().getValue());
    }

    private List<CellToken> getTokens(String origin) {
        List<ITokenizer> tokenizers = Lists.newArrayList(new BlankTokenizer(),
                new BracketTokenizer(), new UnitTokenizer());
        List<CellToken> tokenSumList = Lists.newArrayList();
        for (ITokenizer tokenizer : tokenizers) {
            List<CellToken> tokenList = tokenizer.token(origin);
            if (CollectionUtils.isNotEmpty(tokenList)) {
                tokenSumList.addAll(tokenList);
            }
        }
        List<CellToken> targets = tokenSumList;
        List<IStrainer> strainers = Lists.newArrayList();
        strainers.add(new SuffixStrainer());
        strainers.add(new ContainStrainer());
        for (IStrainer strainer : strainers) {
            targets = strainer.strain(targets);
        }
        System.out.println("strainers:" + ArrayUtils.toString(tokenSumList));
        return targets;
    }

}
