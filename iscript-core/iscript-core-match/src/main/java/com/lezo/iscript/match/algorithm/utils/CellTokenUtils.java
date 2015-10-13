package com.lezo.iscript.match.algorithm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class CellTokenUtils {
    private static final Pattern SIGN_REG = Pattern.compile("^[【】（）\\s]+$");

    /**
     * 不是空白，非特殊字符
     * 
     * @param token
     * @return
     */
    public static boolean isCellToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        Matcher matcher = SIGN_REG.matcher(token);
        return !matcher.find();
    }
}
