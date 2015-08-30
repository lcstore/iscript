package com.lezo.iscript.proxy;

import org.junit.Test;

public class TestIPSeeker {

    @Test
    public void testArea() {
        String fileName = "src/main/resources/config/qqwry.dat";
        IPSeeker seeker = new IPSeeker(fileName, null);
        String ipString = "211.161.248.198";
        ipString = "185.26.183.14";
        ipString = "168.62.191.144";
        System.err.println(seeker.getArea(ipString));
        System.err.println(seeker.getCountry(ipString));
        IPLocation location = seeker.getIPLocation(ipString);
        System.err.println(location.getArea() + ":" + location.getCountry());
    }
}
