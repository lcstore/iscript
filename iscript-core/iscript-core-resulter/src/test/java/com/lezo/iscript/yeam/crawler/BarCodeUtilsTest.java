package com.lezo.iscript.yeam.crawler;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.lezo.iscript.utils.BarCodeUtils;

public class BarCodeUtilsTest {
    @Test
    public void testIsBarCode() {
        Assert.assertEquals(true, BarCodeUtils.isBarCode("9771671216014"));
        Assert.assertEquals(false, BarCodeUtils.isBarCode("6925303722563"));
        Assert.assertEquals(true, BarCodeUtils.isBarCode("6925303722562"));
        Assert.assertEquals(false, BarCodeUtils.isBarCode("692530B722562"));
        Assert.assertEquals(false, BarCodeUtils.isBarCode("925303722562"));
        Assert.assertEquals(true, BarCodeUtils.isBarCode("8801111186100"));
        Assert.assertEquals(true, BarCodeUtils.isBarCode("9555296408395"));
        Assert.assertEquals(true, BarCodeUtils.isBarCode("9325740024200"));
        // http://list.vip.com/508518.html
        // http://a.vpimg4.com/upload/merchandise/pdc/251/104/3100165393496104251/1/3605532996837-5.jpg
        Assert.assertEquals(true, BarCodeUtils.isBarCode("3605532996837"));
        Assert.assertEquals(true, BarCodeUtils.isBarCode("4988888888841"));
        Assert.assertEquals(true, BarCodeUtils.isBarCode("085805521028"));
        Assert.assertEquals(true, BarCodeUtils.isBarCode("027131953241"));
        Assert.assertEquals(true, BarCodeUtils.isBarCode("6938104054671"));
    }

    @Test
    public void testCheckBarCode() throws Exception {
        List<String> lineList = FileUtils.readLines(new File("src/test/resources/barCode.txt"));
        for (String line : lineList) {
            String[] splitStrings = line.split("\t");
            if (!BarCodeUtils.isBarCode(splitStrings[1])) {
                System.out.println(splitStrings[0]);
            }
        }
    }

}
