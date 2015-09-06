package com.lezo.iscript.utils;

import org.apache.commons.lang3.StringUtils;

public class BarCodeUtils {
    private static final int EAN_CODE_LEN = 13;// EAN-13
    private static final int UPC_CODE_LEN = 12;// UPC-12

    public static boolean isUPCode(String barCode) {
        if (StringUtils.isEmpty(barCode)) {
            return false;
        }
        if (barCode.length() != UPC_CODE_LEN) {
            return false;
        }
        return isEANCode("0" + barCode);
    }

    public static boolean isBarCode(String barCode) {
        if (StringUtils.isEmpty(barCode)) {
            return false;
        }
        if (barCode.length() == UPC_CODE_LEN) {
            return isUPCode(barCode);
        }
        return isEANCode(barCode);
    }

    public static boolean isEANCode(String barCode) {
        if (StringUtils.isEmpty(barCode)) {
            return false;
        }
        if (barCode.length() != EAN_CODE_LEN) {
            return false;
        }
        if (!barCode.matches("[0-9]{13}")) {
            return false;
        }
        int oddSum = 0;// 奇数
        int evenSum = 0;// 偶数
        for (int i = 0; i < EAN_CODE_LEN - 1; i++) {
            if (i % 2 == 0) {
                oddSum += Integer.valueOf("" + barCode.charAt(i));
            } else {
                evenSum += Integer.valueOf("" + barCode.charAt(i));
            }
        }
        int sum = evenSum * 3 + oddSum;
        int verifyCode = 10 - sum % 10;
        verifyCode = verifyCode % 10;
        return verifyCode == Integer.valueOf("" + barCode.charAt(EAN_CODE_LEN - 1));
    }

}
