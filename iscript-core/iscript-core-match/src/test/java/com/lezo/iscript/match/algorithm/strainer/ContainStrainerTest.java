package com.lezo.iscript.match.algorithm.strainer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.lezo.iscript.match.algorithm.IStrainer;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BlankTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BracketTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.UnitTokenizer;
import com.lezo.iscript.match.pojo.CellToken;

public class ContainStrainerTest {
    IStrainer strainer = new ContainStrainer();


    @Test
    public void testStrainer01() {
        String origin = "【美孚10w40】美孚（Mobil） 美孚速霸1000机油 10w40 SM级 （4L装）";
        System.out.println("origin:" + origin);
        List<CellToken> tokenSumList = doTokenizer(origin);
        System.out.println("origin.size:" + tokenSumList.size());
        System.out.println("origin.result:" + ArrayUtils.toString(tokenSumList));

        // tokenSumList = new SuffixStrainer().strain(tokenSumList);
        List<CellToken> remains = strainer.strain(tokenSumList);
        System.out.println("strainer.size:" + remains.size());
        System.out.println("strainer.result:" + ArrayUtils.toString(remains));
        String validate = "美孚10w40";
        for (CellToken token : remains) {
            Assert.assertEquals(false, validate.equals(token.getValue()));
        }
    }

    private List<CellToken> doTokenizer(String origin) {
        List<ITokenizer> tokenizers = Lists.newArrayList(new BlankTokenizer(),
                new BracketTokenizer(), new UnitTokenizer());
        List<CellToken> tokenSumList = Lists.newArrayList();
        for (ITokenizer tokenizer : tokenizers) {
            List<CellToken> tokenList = tokenizer.token(origin);
            if (CollectionUtils.isNotEmpty(tokenList)) {
                tokenSumList.addAll(tokenList);
            }
        }
        return tokenSumList;
    }

}