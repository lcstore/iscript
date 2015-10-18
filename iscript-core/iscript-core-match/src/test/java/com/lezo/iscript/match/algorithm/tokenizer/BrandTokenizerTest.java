package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.match.pojo.CellToken;

public class BrandTokenizerTest {
    BrandTokenizer tokenizer = new BrandTokenizer();
    @Test
    public void testBrandTokenizer() {
        String origin = "AVON雅芳水晶鞋喷雾香水 50ml";
        List<CellToken> tokens = tokenizer.token(origin.toLowerCase());
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));
        String validate = "雅芳";
        boolean hasCell = false;
        for (CellToken token : tokens) {
            if (validate.equals(token.getValue())) {
                hasCell = true;
                break;
            }
        }
        Assert.assertEquals(true, hasCell);

    }

}
