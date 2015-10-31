package com.lezo.iscript.utils;

import org.junit.Assert;
import org.junit.Test;

public class BrandUtilsTest {

    @Test
    public void testToUnify() {
        // euro·fit,euro fit,euro.fit
        String origin = "euro·fit";
        String expected = "euro.fit";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
        origin = "euro fit";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
        origin = "euro  fit";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
    }

    @Test
    public void testToUnify02() {
        // euro·fit,euro fit,euro.fit
        String origin = "julie‘s";
        String expected = "julie.s";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
        origin = "julie's";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
    }

    @Test
    public void testToUnify03() {
        // euro·fit,euro fit,euro.fit
        String origin = "f-fook";
        String expected = "f.fook";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
        origin = "f－fook";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
        origin = "sun·maid";
        expected = "sun.maid";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
    }

    @Test
    public void testToUnify04() {
        String origin = "马克·雅可布";
        String expected = "马克.雅可布";
        Assert.assertEquals(expected, BrandUtils.toUnify(origin));
    }
}
