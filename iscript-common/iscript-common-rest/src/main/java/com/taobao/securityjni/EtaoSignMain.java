package com.taobao.securityjni;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;
import com.taobao.securityjni.impl.CImplSecretUtil;
import com.taobao.securityjni.tools.DataContext;

public class EtaoSignMain {

    public static void main(String[] args) throws Exception {
        Map<String, String> map = Maps.newHashMap();
        map.put("IMSI", "460011607507288");
        map.put("TIME", "1447156729072");
        map.put("API", "mtop.etao.kaka.barcode.search");
        map.put("V", "2.0");
        map.put("IMEI", "861000020674881");
        map.put("DATA", "{\"content\":\"6921665709012\",\"type\":\"EAN8\",\"gps\":\"0,0\"}");

        String data = map.get("DATA");
        StringBuilder localStringBuilder = new StringBuilder();
        Iterator<String> localIterator = map.keySet().iterator();
        while (localIterator.hasNext())
        {
            String str1 = (String) localIterator.next();
            String str2 = (String) map.get(str1);
            if (str2 != null)
            {
                localStringBuilder.append(str1).append('=').append(str2);
                localStringBuilder.append('^');
            }
        }
        byte[] localBytes = localStringBuilder.substring(0, localStringBuilder.length() - 1).getBytes("UTF-8");
        byte[] dataBytes = data.getBytes();
        DataContext paramDataContext = new DataContext();
        String appKey = "21702307";
        paramDataContext.extData = appKey.getBytes();
        // byte[] bytes = TaobaoUtils.encryptMD5(source);
        String sign = CImplSecretUtil.getSign(localBytes, dataBytes, paramDataContext);
        sign = sign.toLowerCase();
        System.err.println("sign:" + sign);
        // Assert.assertEquals("8f4bf0b894ab0a8e6e0de10dc46460a9", sign);
    }
}
