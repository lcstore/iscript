package com.lezo.iscript.utils;

import org.junit.Test;

public class IPSeekerTest {

    @Test
    public void testArea() {
        IPSeeker seeker = new IPSeeker();
        String ipString = "211.161.248.198";
        ipString = "185.26.183.14";
        ipString = "168.62.191.144";
        ipString = "54.145.248.144";
        IPLocation location = seeker.getIPLocation(ipString);
        System.err.println(location.getArea() + ":" + location.getCountry());
    }
}
