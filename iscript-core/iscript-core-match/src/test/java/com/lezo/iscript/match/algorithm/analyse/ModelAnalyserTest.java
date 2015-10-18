package com.lezo.iscript.match.algorithm.analyse;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellTokenUtils;

public class ModelAnalyserTest {
    ModelAnalyser analyser = new ModelAnalyser();

    @Test
    public void testAnalyser() {
        String origin = "【良兴LX-561】良兴 LX-561 中英文双语儿童早教机 120页彩色学习卡片 兰色 【行情 报价 价格 评测】";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("lx-561", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser02() {
        String origin = "康师傅3+2酥松香草奶油";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("3+2", assort.getValue().getValue().getValue());
    }
}
