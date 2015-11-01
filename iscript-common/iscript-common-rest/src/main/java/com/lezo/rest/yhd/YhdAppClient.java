package com.lezo.rest.yhd;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.collect.Maps;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;

public class YhdAppClient {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

    public YhdAppClient() {
    }

    public String getUrl(String bCode) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String timestamp = "" + System.currentTimeMillis() / 1000;
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
        String signature = new String(doCharMap(md.digest(newParams.getBytes())));

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
        return urlBuffer.toString();
    }

    private static final char[] CHAR_MAP = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };

    public static char[] doCharMap(byte[] paramArrayOfByte)
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
            arrayOfChar[j] = CHAR_MAP[((paramArrayOfByte[i] & 0xF0) >>> 4)];
            j = m + 1;
            arrayOfChar[m] = CHAR_MAP[(paramArrayOfByte[i] & 0xF)];
            i += 1;
        }
    }

    public String execute(String barCode, Map<String, String> paramMap) throws Exception {
        String sUrl = getUrl(barCode);
        HttpPost httpPost = new HttpPost(sUrl);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Entry<String, String> entry : paramMap.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(params, "UTF-8");
        httpPost.setEntity(paramEntity);
        return HttpClientUtils.getContent(client, httpPost);
    }

}
