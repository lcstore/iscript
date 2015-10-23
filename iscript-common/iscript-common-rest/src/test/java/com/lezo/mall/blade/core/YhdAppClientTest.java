package com.lezo.mall.blade.core;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

public class YhdAppClientTest {

    @Test
    public void testUrl() {
        String bCode = "6948939610478";
        String key =
                "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANdAbyufGGyWb7Z/sSm7dsHSKeucJpMeFRAJgT7Zf6pZCBymf4e/DKSzcDrua+QhzK9XQQPccUN/4A2gxZXUTHnp0qo6ZJSaOuTuzlyNB6IizFftDeFLM7rX+Quko0f40TqIA+mb3oWDXmsKNub7mtH3oOEs0bvO3YmA53ajXBeRAgMBAAECgYEAwXj+83xqnZ+SBb08ZkBDe+8FEuslmPJfCC0i6HTiVSD1M5tL4Z2NJbTLWYzXmRPwQGHy5B+OBpe3sUgikItjuEKR4PRAd2vtkTErnbPxwautgtgDyYLxn6IfuQqTYbxso2yZTO7CCb7QhL2gRfpfhYgfKaoH/4MC48FSEmB+xmECQQDxozXVZ2bqETK1XGECIYqWnWx64UUEA+K4pvbIU+MBwpzQyk9CKKJ5q00x+SuJT6/Jov+a3dSOk4Qyrzl9Hz2tAkEA5Au7vrnNCsnCgD7ItMCSM0LJdFGCX4//z22jXsow5KwajQXqLQEYTmp/l5q6R2kGa2eyOrSuWaH6gmf1Tx519QJAFEV27LJCBfzvXhuj38PkloIaaaygV5fj203WgjPXZXxoH3P5djlmeAKQ9VJL/rb6rlXIT7uwa02g14evsPl/+QJBALb2pwImBlmCeNf2B4fl/Sa9je4SO3y6hu6As5Oou0OsxXyh4zmKaFr53TbggFYs8GaaAwhQ0JW/fMLF764z7UUCQDKhKa4GuMpdEq0SDzXAWcSGydq315B5KAe+qfCS1kv5aPn04nKwv09B1C3Tbu6UWjf52kK//kZ63n/Uv4HWwDs=";
        key =
                "Ius079KKQRmIpj3eoCzbFlRwCg8cnqymYWcy3vObVskI6pMm6Fa1s4apZqYuFigOJ2VdPny4CB0acjssW0eQU7szKaTlB8IAs8WsrXO8KYPulZ9Ar9a0fr2g6ZAxKa1WfSb/VmFZJOcLoiaAp38z3Z6UJQSKCzH3hTpBYMycT+c=";
        String param = toHttpParam(bCode, key);
        String baseUrl = "http://mapi.yhd.com/search/getProductByBarcodeWithPMS/v1.3.8?";
        System.err.println(baseUrl + param);
        System.err.println("1445591459936" + ":" + System.currentTimeMillis());
    }

    public String toHttpParam(String bCode, String key) {
        Map<String, String> paramMap = new HashMap<String, String>();
        // paramMap.put("methodName", "getProductByBarcodeWithPMS/v1.3.8");
        paramMap.put("methodName", "getProductByBarcodeWithPMS%2Fv1.3.8");
        paramMap.put("methodBody", "");
        paramMap.put("barcode", bCode);
        paramMap.put("guid", "0");
        paramMap.put("signature_method", "md5");
        paramMap.put("timestamp", getServerStamp());
        paramMap.put("trader", "androidSystem");
        paramMap.put("signature", getSign(key, paramMap));

        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : paramMap.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    private String getSign(String key, Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder(key);
        Map<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(paramMap);
        Iterator<String> iter = treeMap.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            sb.append(name.toLowerCase(Locale.US)).append(paramMap.get(name));
        }
        String source = sb.toString();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            char[] destChar = toMapChars(md.digest(source.getBytes("utf-8")));
            return new String(destChar);
        } catch (Exception e) {
            throw new RuntimeException("Sign,cause:", e);
        }
    }

    private static final char[] MAP_CHARS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };

    public static char[] toMapChars(byte[] srcBytes) {
        int j = 0;
        int k = srcBytes.length;
        char[] destChars = new char[k << 1];
        int i = 0;
        for (;;) {
            if (i >= k) {
                return destChars;
            }
            int m = j + 1;
            destChars[j] = MAP_CHARS[((srcBytes[i] & 0xF0) >>> 4)];
            j = m + 1;
            destChars[m] = MAP_CHARS[(srcBytes[i] & 0xF)];
            i += 1;
        }
    }

    private String getServerStamp() {
        long stamp = System.currentTimeMillis();
        return (stamp / 1000L) + "";
    }
}
