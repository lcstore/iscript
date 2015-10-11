package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.match.pojo.CellToken;

public class UnitTokenizerTest {
    UnitTokenizer tokenizer = new UnitTokenizer();

    @Test
    public void testUnitTokenizer_en() {
        String origin = "肌言堂 水漾肌HA精华原液30ml/瓶";
        List<CellToken> tokens = tokenizer.token(origin);
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));
        String validate = "30ml";
        boolean hasCell = false;
        for (CellToken token : tokens) {
            if (validate.equals(token.getToken())) {
                hasCell = true;
                break;
            }
        }
        Assert.assertEquals(true, hasCell);

    }

    @Test
    public void testUnitTokenizer_cn() {
        // 单位前是字母，目前无法切出单位
        // String origin = "Depend得伴 成人纸尿裤（超强吸收型）中号M10片×6包";
        String origin = "Depend得伴 成人纸尿裤（超强吸收型）中号M纸尿裤 10片×6包";
        List<CellToken> tokens = tokenizer.token(origin);
        System.out.println("origin:" + origin);
        System.out.println("size:" + tokens.size());
        System.out.println("result:" + ArrayUtils.toString(tokens));
        String validate = "10片";
        boolean hasCell = false;
        for (CellToken token : tokens) {
            if (validate.equals(token.getToken())) {
                hasCell = true;
                break;
            }
        }
        Assert.assertEquals(true, hasCell);

    }

}
