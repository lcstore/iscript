package com.lezo.iscript.match.algorithm.analyse;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellAssortUtils;
import com.lezo.iscript.match.utils.CellTokenUtils;

public class AssortTest {

    @Test
    public void testRemoveAssort() {
        String origin = "AVON雅芳水晶鞋喷雾香水 50ml";
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        CellAssort assort = brandAnalyser.analyse(tokens);
        Assert.assertEquals("雅芳", assort.getValue().getValue().getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        Assert.assertEquals("50ml", assort.getValue().getValue().getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        Assert.assertEquals(true, null == assort.getValue());
    }

    @Test
    public void testRemoveAssort2() {
        String origin = "意大利进口 Ferrero Rocher费列罗榛果威化巧克力24粒钻石装300g【本产品不含礼品袋，请以收到实物为准】";
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        CellAssort assort = brandAnalyser.analyse(tokens);
        Assert.assertEquals("费列罗", assort.getValue().getValue().getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        Assert.assertEquals("300g", assort.getValue().getValue().getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        Assert.assertEquals(true, null == assort.getValue());
    }

    @Test
    public void testRemoveAssort02() {
        String origin = "小霸王电视游戏机D31 白色";
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        CellAssort assort = brandAnalyser.analyse(tokens);
        Assert.assertEquals("小霸王", assort.getValue().getValue().getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        Assert.assertEquals(true, null == assort.getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        Assert.assertEquals("d31", assort.getValue().getValue().getValue());
    }

    @Test
    public void testRemoveAssort03() {
        String origin = "白裙之恋香体乳150克";
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        CellAssort assort = brandAnalyser.analyse(tokens);
        Assert.assertEquals(true, assort.getValue() == null);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        Assert.assertEquals("150克", assort.getValue().getValue().getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        Assert.assertEquals(true, assort.getValue() == null);
    }

    @Test
    public void testRemoveAssort04() {
        String origin = "克特多金象黑巧克力 片装240克";
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        CellAssort assort = brandAnalyser.analyse(tokens);
        Assert.assertEquals("克特多金象", assort.getValue().getValue().getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        Assert.assertEquals("240克", assort.getValue().getValue().getValue());
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        Assert.assertEquals(true, assort.getValue() == null);
    }
}
