package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.pojo.CellToken;

public class BlankTokenizerTest {
    ITokenizer tokenizer = new BlankTokenizer();

    @Test
    public void testBlankTokenizer() {
        String origin = "【良兴LX-561】良兴 LX-561 中英文双语儿童早教机 120页彩色学习卡片 兰色 【行情 报价 价格 评测】";
        List<CellToken> tokens = tokenizer.token(origin);
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));

    }

    @Test
    public void testBlankTokenizer_one() {
        String origin = "雅芳白裙之恋香体乳150克";
        List<CellToken> tokens = tokenizer.token(origin);
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));

    }
}
