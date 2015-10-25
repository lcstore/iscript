package com.lezo.iscript.match.utils;

import java.util.List;

import com.google.common.collect.Lists;
import com.lezo.iscript.match.algorithm.similar.BarCodeSimilar;
import com.lezo.iscript.match.algorithm.similar.BrandSimilar;
import com.lezo.iscript.match.algorithm.similar.ModelSimilar;
import com.lezo.iscript.match.algorithm.similar.UnitSimilar;
import com.lezo.iscript.match.algorithm.similar.WareSimilar;
import com.lezo.iscript.match.pojo.SimilarFact;

public class SimilarFactUtils {
    private static List<SimilarFact> factList;

    public static List<SimilarFact> getDefaultFacts() {
        if (factList != null) {
            return factList;
        }
        factList = Lists.newArrayList();
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
}
