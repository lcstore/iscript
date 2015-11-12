package com.taobao.securityjni.impl;

import com.taobao.securityjni.tools.DataContext;

public class CImplSecretUtil {
    private native byte[] getExternalSignByte(byte[] paramArrayOfByte, DataContext paramDataContext);

    private native byte[] getQianNiuSignByte(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

    private native String
            getSignNative(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, DataContext paramDataContext);

    private native byte[] getTopSignByte(byte[] paramArrayOfByte, DataContext paramDataContext);

    private native String getTopToken(byte[] paramArrayOfByte, String paramString, DataContext paramDataContext);

    static {
        System.load("SSECeg-1.2.7");
    }

    public static String getSign(byte[] localBytes, byte[] dataBytes, DataContext paramDataContext) {
        CImplSecretUtil secretUtil = new CImplSecretUtil();
        return secretUtil.getSignNative(localBytes, dataBytes, paramDataContext);
    }
}
