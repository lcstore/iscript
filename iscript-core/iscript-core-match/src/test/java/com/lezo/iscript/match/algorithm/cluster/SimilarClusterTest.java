package com.lezo.iscript.match.algorithm.cluster;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.algorithm.analyse.BrandAnalyser;
import com.lezo.iscript.match.algorithm.analyse.ModelAnalyser;
import com.lezo.iscript.match.algorithm.analyse.UnitAnalyser;
import com.lezo.iscript.match.algorithm.similar.BarCodeSimilar;
import com.lezo.iscript.match.algorithm.similar.BrandSimilar;
import com.lezo.iscript.match.algorithm.similar.ModelSimilar;
import com.lezo.iscript.match.algorithm.similar.UnitSimilar;
import com.lezo.iscript.match.algorithm.similar.WareSimilar;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.pojo.SimilarCenter;
import com.lezo.iscript.match.pojo.SimilarFact;
import com.lezo.iscript.match.pojo.SimilarIn;
import com.lezo.iscript.match.utils.CellAssortUtils;
import com.lezo.iscript.match.utils.CellTokenUtils;

public class SimilarClusterTest {

    @Test
    public void testCluster() {
        SimilarCluster cluster = new SimilarCluster();
        List<SimilarFact> similarMap = newSimilarFacts();
        List<SimilarIn> similarIns = Lists.newArrayList();
        SimilarIn similarIn = newSimilarIn("【雅芳香水】雅芳酷爽香水 莓果爽 30毫升 【行情 报价 价格 评测】", "6922829568803", "1001_358219");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("雅芳酷爽香水   莓果爽 30ml", "", "1002_1054363");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("雅芳卸妆油 200ml", "", "1002_1054346");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("AVON雅芳 卸妆油200ML", "", "1015_60035702");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("AVON 雅芳卸妆油 200ml-化妆-卓越亚马逊", "", "1003_B005OOYN46");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("雅芳卸妆乳 200ml", "6907376501336", "1002_1054347");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("雅芳凝白修护乳液 75ml", "", "1002_1054356");// 无匹配
        similarIns.add(similarIn);
        List<SimilarCenter> centers = cluster.doCluster(similarIns, similarMap);

        System.err.println("-----------------\n");
        for (SimilarCenter ct : centers) {
            System.err.println("center:" + ct.getValue() + ",count:" + ct.getOuts().size());
            System.err.println("outs:" + ArrayUtils.toString(ct.getOuts()));
        }
    }

    private List<SimilarFact> newSimilarFacts() {
        List<SimilarFact> factList = Lists.newArrayList();
        SimilarFact fact = new SimilarFact();
        fact.setName("wareCode");
        fact.setSimilar(new WareSimilar());
        fact.setFact(0.1F);
        factList.add(fact);
        fact = new SimilarFact();
        fact.setName("barCode");
        fact.setSimilar(new BarCodeSimilar());
        fact.setFact(0.2F);
        factList.add(fact);
        fact = new SimilarFact();
        fact.setName("tokenBrand");
        fact.setSimilar(new BrandSimilar());
        fact.setFact(0.4F);
        factList.add(fact);
        fact = new SimilarFact();
        fact.setName("tokenModel");
        fact.setSimilar(new ModelSimilar());
        fact.setFact(0.15F);
        factList.add(fact);
        fact = new SimilarFact();
        fact.setName("tokenUnit");
        fact.setSimilar(new UnitSimilar());
        fact.setFact(0.15F);
        factList.add(fact);
        return factList;
    }

    @Test
    public void testCluster02() {
        SimilarCluster cluster = new SimilarCluster();
        List<SimilarFact> similarMap = newSimilarFacts();
        List<SimilarIn> similarIns = Lists.newArrayList();
        SimilarIn similarIn = newSimilarIn("康师傅 3+2酥松(巧克力牛奶味)354g/包", "", "1002_1049478");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("康师傅3+2酥松夹心饼干巧克力牛奶味354g*2", "", "1003_B0050GFTWS");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("康师傅 3+2酥松(花生巧克力味)354g/包", "", "1002_1049479");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("康师傅3+2酥松夹心(花生巧克力味 袋装354g)", "", "1006_511094");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("【天猫超市】康师傅3+2酥松夹心花生巧克力味（袋装354G）", "", "1063_14863799798");
        similarIns.add(similarIn);
        similarIn = newSimilarIn("康师傅乐芙球巧克力60g", "6919892441102", "1001_859451");
        similarIns.add(similarIn);
        List<SimilarCenter> centers = cluster.doCluster(similarIns, similarMap);
        for (SimilarCenter ct : centers) {
            System.err.println(ct);
        }
    }

    private SimilarIn newSimilarIn(String productName, String barCode, String skuCode) {
        SimilarIn newIn = new SimilarIn();
        newIn.setSkuCode(skuCode);
        newIn.setProductName(productName);
        newIn.setBarCode(CellAssortUtils.toAssort(barCode));
        tokenizer(newIn);
        return newIn;
    }

    private void tokenizer(SimilarIn newIn) {
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(newIn.getProductName().toLowerCase());
        CellAssort assort = brandAnalyser.analyse(tokens);
        newIn.setTokenBrand(assort);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        newIn.setTokenUnit(assort);
        newIn.setTokenUnit(assort);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        newIn.setTokenModel(assort);

    }
}
