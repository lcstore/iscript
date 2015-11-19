package com.lezo.rest.dangdang;

import java.security.MessageDigest;

public class DangRestClient {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        String timestamp = "1447574390";
        sb.append("list_isbn_product").append(",").append(timestamp).append(",XinXF,")
                .append("f6cb965617ddf6a2cc605e63986cbe76");
        String encode = encode(sb.toString());
        String target = "758b49150e03afaef3616e83007888f7";
        System.err.println(encode + ",equals:" + target.equals(encode));
    }

    public String getEncode(String method) {
        StringBuilder sb = new StringBuilder();
        String timestamp = "1447564096";
        long stamp = System.currentTimeMillis() / 1000;
        timestamp = "" + stamp;
        sb.append(method).append(",").append(timestamp).append(",XinXF,")
                .append("eb9814ec35eb30b2bb565460f1d35ca4");
        String encode = encode("abtest,1447574759,XinXF,f6cb965617ddf6a2cc605e63986cbe76");
        return encode;
    }

    private static final char[] chars = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };

    public static String encode(String paramString)
    {
        int j = 0;
        try
        {
            Object localObject = MessageDigest.getInstance("MD5");
            ((MessageDigest) localObject).update(paramString.getBytes("UTF-8"));
            byte[] mBytes = ((MessageDigest) localObject).digest();
            int k = mBytes.length;
            char[] destChars = new char[k << 1];
            int i = 0;
            while (i < k)
            {
                int m = j + 1;
                destChars[j] = chars[((mBytes[i] & 0xF0) >>> 4)];
                j = m + 1;
                destChars[m] = chars[(mBytes[i] & 0xF)];
                i += 1;
            }
            return new String(destChars);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}
