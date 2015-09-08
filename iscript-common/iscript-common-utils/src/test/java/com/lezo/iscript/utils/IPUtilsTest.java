package com.lezo.iscript.utils;

import org.junit.Test;

public class IPUtilsTest {

    @Test
    public void testLocation() {
        String ipString = "211.161.248.198";
        ipString = "185.26.183.14";
        ipString = "168.62.191.144";
        ipString = "54.145.248.144";
        ipString = "115.231.94.62";
        IPLocation location = IPUtils.getLocation(ipString);
        System.err.println(location.getArea() + ":" + location.getCountry());
    }
}
