package com.lezo.iscript.match.algorithm.analyse;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellTokenUtils;

public class UnitAnalyserTest {
    UnitAnalyser analyser = new UnitAnalyser();

    @Test
    public void testAnalyser() {
        String origin = "【美孚10w40】美孚（Mobil） 美孚速霸1000机油 10w40 SM级 （4L装） 【行情 报价 价格 评测】";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("4l", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser02() {
        String origin = "3M 320C 思高双面泡棉胶带 高效型 24毫米×5.5米";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("24毫米×5.5米", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser03() {
        String origin = "克特多金象黑巧克力 片装240克";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("240克", assort.getValue().getValue().getValue());
    }

}
