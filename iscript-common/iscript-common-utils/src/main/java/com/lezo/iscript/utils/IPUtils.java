package com.lezo.iscript.utils;

import org.apache.commons.lang3.StringUtils;

public class IPUtils {
    private static IPSeeker seeker;

    public static IPLocation getLocation(String ipString) {
        if (StringUtils.isBlank(ipString)) {
            IPLocation location = new IPLocation();
            location.setArea("未知");
            location.setCountry("未知");
            return location;
        }
        return getSeeker().getIPLocation(ipString);
    }

    private static IPSeeker getSeeker() {
        if (seeker == null) {
            synchronized (IPUtils.class) {
                if (seeker == null) {
                    seeker = new IPSeeker();
                }
            }
        }
        return seeker;
    }
}
