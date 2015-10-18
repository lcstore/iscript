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
        System.err.println("before.token:" + tokens);
        CellAssort assort = brandAnalyser.analyse(tokens);
        System.err.println("brand.assort:" + assort);
        System.err.println("brand.token:" + tokens);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        System.err.println("unit.assort:" + assort);
        System.err.println("unit.token:" + tokens);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        System.err.println("model.assort:" + assort);
    }

    @Test
    public void testRemoveAssort02() {
        String origin = "小霸王电视游戏机D31 白色";
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.err.println("before.token:" + tokens);
        CellAssort assort = brandAnalyser.analyse(tokens);
        System.err.println("brand.assort:" + assort);
        System.err.println("brand.token:" + tokens);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        System.err.println("unit.assort:" + assort);
        System.err.println("unit.token:" + tokens);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        System.err.println("model.assort:" + assort);
        Assert.assertEquals("d31", assort.getValue().getValue().getValue());
    }

    @Test
    public void testRemoveAssort03() {
        String origin = "白裙之恋香体乳150克";
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(origin.toLowerCase());
        System.err.println("before.token:" + tokens);
        CellAssort assort = brandAnalyser.analyse(tokens);
        System.err.println("brand.assort:" + assort);
        System.err.println("brand.token:" + tokens);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        System.err.println("unit.assort:" + assort);
        System.err.println("unit.token:" + tokens);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        System.err.println("model.assort:" + assort);
        Assert.assertEquals(true, assort.getValue() == null);
    }
}
