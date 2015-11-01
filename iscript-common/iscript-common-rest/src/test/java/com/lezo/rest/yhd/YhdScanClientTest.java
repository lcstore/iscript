package com.lezo.rest.yhd;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

public class YhdScanClientTest {

    @Test
    public void testBarCode() throws Exception {
        String paramLink =
                "barcode=6948939610478guid=0methodbody=methodname=getProductByBarcodeWithPMS/v1.3.8signature_method=md5timestamp=1445757657trader=androidSystemwwwdhsm6";
        String destVal = "ED5F30B5893FB7F20A1892910F49D57A";

        MessageDigest md = MessageDigest.getInstance("MD5");
        // String signature = byte2hex(md.digest(paramLink.toString().getBytes("utf-8")));
        String signature = new String(a(md.digest(paramLink.getBytes())));
        System.err.println("signature:" + signature);
        Assert.assertEquals(destVal, signature);
    }

    @Test
    public void testBarCodeParam() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        System.err.println("timestamp:" + System.currentTimeMillis());
        map.put("timestamp", "1445757657");
        map.put("guid", "0");
        map.put("methodBody", "");
        map.put("signature_method", "md5");
        map.put("barcode", "6948939610478");
        map.put("trader", "androidSystem");
        map.put("methodName", "getProductByBarcodeWithPMS/v1.3.8");
        String sKey = "wwwdhsm6";
        TreeMap<String, String> treeMap = Maps.newTreeMap();
        treeMap.putAll(map);
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : treeMap.entrySet()) {
            sb.append(entry.getKey().toLowerCase(Locale.US)).append("=").append(entry.getValue());
        }
        sb.append(sKey);
        String paramLink =
                "barcode=6948939610478guid=0methodbody=methodname=getProductByBarcodeWithPMS/v1.3.8signature_method=md5timestamp=1445757657trader=androidSystemwwwdhsm6";
        String newParams = sb.toString();
        Assert.assertEquals(paramLink, newParams);
        String destVal = "ED5F30B5893FB7F20A1892910F49D57A";

        MessageDigest md = MessageDigest.getInstance("MD5");
        String signature = new String(a(md.digest(newParams.getBytes())));
        System.err.println("signature:" + signature);
        Assert.assertEquals(destVal, signature);
    }

    @Test
    public void testBarCodeUrl() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        System.err.println("timestamp:" + System.currentTimeMillis());
        String timestamp = "" + System.currentTimeMillis() / 1000;
        String bCode = "6948939610478";
        bCode = "8000500003787";
        map.put("timestamp", timestamp);
        map.put("guid", "0");
        map.put("methodBody", "");
        map.put("signature_method", "md5");
        map.put("barcode", bCode);
        map.put("trader", "androidSystem");
        map.put("methodName", "getProductByBarcodeWithPMS/v1.3.8");
        String sKey = "wwwdhsm6";
        TreeMap<String, String> treeMap = Maps.newTreeMap();
        treeMap.putAll(map);
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : treeMap.entrySet()) {
            sb.append(entry.getKey().toLowerCase(Locale.US)).append("=").append(entry.getValue());
        }
        sb.append(sKey);
        String newParams = sb.toString();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String signature = new String(a(md.digest(newParams.getBytes())));

        map.put("signature", signature);
        StringBuffer urlBuffer = new StringBuffer("http://mapi.yhd.com/search/getProductByBarcodeWithPMS/v1.3.8?");
        Iterator<Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            urlBuffer.append(entry.getKey()).append("=").append(entry.getValue());
            if (it.hasNext()) {
                urlBuffer.append("&");
            }
        }
        String sDestUrl = urlBuffer.toString();
        System.err.println(sDestUrl);
    }

    private static final char[] a = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };

    public static char[] a(byte[] paramArrayOfByte)
    {
        int j = 0;
        int k = paramArrayOfByte.length;
        char[] arrayOfChar = new char[k << 1];
        int i = 0;
        for (;;)
        {
            if (i >= k) {
                return arrayOfChar;
            }
            int m = j + 1;
            arrayOfChar[j] = a[((paramArrayOfByte[i] & 0xF0) >>> 4)];
            j = m + 1;
            arrayOfChar[m] = a[(paramArrayOfByte[i] & 0xF)];
            i += 1;
        }
    }
}
