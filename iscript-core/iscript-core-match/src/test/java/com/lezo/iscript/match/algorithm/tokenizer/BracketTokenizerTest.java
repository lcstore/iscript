package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.match.pojo.CellToken;

public class BracketTokenizerTest {
    BracketTokenizer tokenizer = new BracketTokenizer();

    @Test
    public void testBracketTokenizer_cn() {
        String origin = "【美孚10w40】美孚（Mobil） 美孚速霸1000机油 10w40 SM级 （4L装） 【行情 报价 价格 评测】";
        List<CellToken> tokens = tokenizer.token(origin);
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));
        String validate = "Mobil";
        boolean hasCell = false;
        for (CellToken token : tokens) {
            if (validate.equals(token.getValue())) {
                hasCell = true;
                break;
            }
        }
        Assert.assertEquals(true, hasCell);

    }

    @Test
    public void testBracketTokenizer_en() {
        String origin = "[当当自营] 三珍斋鲜肉粽200g*3(粽子)";
        List<CellToken> tokens = tokenizer.token(origin);
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));
        String validate = "粽子";
        boolean hasCell = false;
        for (CellToken token : tokens) {
            if (validate.equals(token.getValue())) {
                hasCell = true;
                break;
            }
        }
        Assert.assertEquals(true, hasCell);

    }

    @Test
    public void testBracketTokenizer_both() {
        String origin = "EXCO 宜适酷  (WZM15) 高清保护贴 For （三星P3100）";
        List<CellToken> tokens = tokenizer.token(origin);
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));
        String cnValidate = "三星P3100";
        String enValidate = "WZM15";
        boolean hasCn = false;
        boolean hasEn = false;
        for (CellToken token : tokens) {
            if (cnValidate.equals(token.getValue())) {
                hasCn = true;
            }
            if (enValidate.equals(token.getValue())) {
                hasEn = true;
            }
        }
        Assert.assertEquals(true, hasCn);
        Assert.assertEquals(true, hasEn);
    }

    @Test
    public void testBracketTokenizer_mid() {
        String origin = "【雅培奶粉】Abbott 雅培金装喜康力2段较大婴儿配方奶粉[1200]克（3联包）【行情 报价 价格 评测】";
        List<CellToken> tokens = tokenizer.token(origin);
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));
        String cnValidate = "雅培奶粉";
        String enValidate = "1200";
        boolean hasCn = false;
        boolean hasEn = false;
        for (CellToken token : tokens) {
            if (cnValidate.equals(token.getValue())) {
                hasCn = true;
            }
            if (enValidate.equals(token.getValue())) {
                hasEn = true;
            }
        }
        Assert.assertEquals(true, hasCn);
        Assert.assertEquals(true, hasEn);
    }

}
