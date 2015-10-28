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
        Assert.assertEquals("佳乐锭", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser06() {
        String origin = "印度尼西亚Pepperidge Farm非凡农庄法式香草夹心威化卷382g ";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("非凡农庄", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser07() {
        String origin = "比利时LOTUS 和情焦糖饼干250g";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("和情", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser08() {
        String origin = "比利时进口 Jules Destrooper 茱莉斯 布鲁日河 饼干 礼盒 350g 盒装";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("茱莉斯", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser09() {
        String origin = "Nissin日清 合味道海鲜味油炸方便面杯面 75g 香港进口";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("日清", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser10() {
        String origin = "Meiji明治 熊猫双重巧克力夹心饼干 50g 新加坡进口";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("明治", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser11() {
        String origin = "MACAU WINGFAI永辉 迷你杏仁饼 120g（7包）独立包装";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("永辉", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser12() {
        String origin = "East Taste 安心味觉 一口凤梨酥礼盒 300g 台湾地区进口";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("安心味觉", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser13() {
        String origin = "柏龙 慕尼黑酵母型小麦啤酒 330ml/瓶*24 德国原装进口";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("柏龙", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser14() {
        String origin = "宝鼎 海鸥康乐醋（枸杞）500ml/瓶";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("宝鼎", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser15() {
        String origin = "BETTY CROCKER 贝蒂妙厨 香草味蛋糕涂层（烘焙用） 453g 美国进口";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("贝蒂妙厨", assort.getValue().getValue().getValue());
    }

    @Test
    public void testAnalyser16() {
        String origin = "Blue Diamond蓝钻石 蜜烤风味扁桃仁 170g 美国进口";
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("tokens:" + ArrayUtils.toString(tokens));
        CellAssort assort = analyser.analyse(tokens);
        System.out.println("assort.value:" + assort.getValue());
        System.out.println("assort.size:" + assort.getStats().size());
        System.out.println("assort.stat:" + assort.getStats());
        Assert.assertEquals("蓝钻石", assort.getValue().getValue().getValue());
    }

}
