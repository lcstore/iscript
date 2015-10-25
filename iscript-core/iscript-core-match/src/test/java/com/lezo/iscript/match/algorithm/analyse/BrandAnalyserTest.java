package com.lezo.iscript.match.algorithm.analyse;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellTokenUtils;

public class BrandAnalyserTest {
    BrandAnalyser analyser = new BrandAnalyser();

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
        Assert.assertEquals("美孚", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser02() {
        String origin = "婵真银杏天然精华液50ml";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("婵真", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser03() {
        String origin = "小霸王电视游戏机D31 白色";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("小霸王", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser04() {
        String origin = "【迪宝乐积木】迪宝乐 电子积木之奥运旗舰号2008拼 【行情 报价 价格 评测】";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("迪宝乐", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser05() {
        String origin = "意大利进口 Galatine佳乐锭/阿拉丁巧克力味牛奶片 100g";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("阿拉丁", assort.getValue().getValue().getValue());
    }

}
