package com.lezo.iscript.utils;

public class BrandUtils {

    public static String toUnify(String origin) {
        if (origin == null) {
            return null;
        }
        origin = origin.replaceAll("[\\s\\-]+", ".");
        origin = origin.replaceAll("[·‘'·－]+", ".");
        return origin;
    }
}
