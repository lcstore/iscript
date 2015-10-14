package com.lezo.iscript.match.algorithm;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.google.common.collect.Lists;

public class CmpTest {

    @Test
    public void testCnTop() {
        final Pattern CN_REG = Pattern.compile("[\u4e00-\u9fa5]+");
        Comparator<String> cmp = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                boolean hasCn1 = CN_REG.matcher(o1).find();
                boolean hasCn2 = CN_REG.matcher(o2).find();
                if (hasCn1 && !hasCn2) {
                    return -1;
                } else if (!hasCn1 && hasCn2) {
                    return 1;
                }
                return o2.length() - o1.length();
            }
        };

        List<String> dataList = Lists.newArrayList("123", "我123", "12CND", "我们的32");
        Collections.sort(dataList, cmp);
        System.err.println(ArrayUtils.toString(dataList));
    }
}
