package com.lezo.rest.yhd;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.rest.SignBuildable;

public class YhdRestClient {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SignBuildable builder;
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private String appKey;
    private String appSecret;
    private String accessToken;
    private String serverUrl = "http://openapi.yhd.com/app/api/rest/router";

    public YhdRestClient(String appKey, String appSecret, String accessToken) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.accessToken = accessToken;
        this.builder = new MD5SignBuilder(this.appSecret);
    }

    // 系统级参数设置（必须）
    public Map<String, Object> createSystemParaMap() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String timestamp = sdf.format(new Date());
        paramMap.put("appKey", appKey);
        if (StringUtils.isNotEmpty(accessToken)) {
            paramMap.put("sessionKey", accessToken);
        }
        paramMap.put("timestamp", timestamp);
        // 根据实际调用填参数
        paramMap.put("format", "json");
        paramMap.put("method", "");
        paramMap.put("ver", "1.0");
        return paramMap;
    }

    public String execute(String url, Map<String, Object> paramMap) throws Exception {
        if (StringUtils.isBlank(url)) {
            url = this.serverUrl;
        }
        String sign = builder.getSign(paramMap);
        paramMap.put("sign", sign);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Entry<String, Object> entry : paramMap.entrySet()) {
            if ("sessionKey".equals(entry.getKey())) {
                continue;
            }
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        HttpPost httpPost = new HttpPost(url);
        UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(params, "UTF-8");
        httpPost.setEntity(paramEntity);
        return HttpClientUtils.getContent(client, httpPost);
    }

    class MD5SignBuilder implements SignBuildable {

        private String appSecret;

        public MD5SignBuilder(String appSecret) {
            super();
            this.appSecret = appSecret;
        }

        @Override
        public String getSign(Map<String, Object> inMap) throws Exception {
            return md5Signature(inMap, this.appSecret);
        }

        private void ascentOrder(List<Entry<String, Object>> inList) {
            Collections.sort(inList, new Comparator<Entry<String, Object>>() {
                @Override
                public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
        }

        /**
         * 新的md5签名，首尾放secret。
         *
         * @param params 传给服务器的参数
         *
         * @param secret 分配给您的APP_SECRET
         */
        public String md5Signature(Map<String, Object> params, String secret) {
            String result = null;
            StringBuffer orgin = getBeforeSign(params, new StringBuffer(secret));
            if (orgin == null)
                return result;

            // secret last
            orgin.append(secret);
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));

            } catch (Exception e) {
                throw new java.lang.RuntimeException("sign error !");
            }
            return result;
        }

        /**
         * 二进制转字符串
         */
        private String byte2hex(byte[] b) {

            StringBuffer hs = new StringBuffer();
            String stmp = "";
            for (int n = 0; n < b.length; n++) {
                stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
                if (stmp.length() == 1)
                    hs.append("0").append(stmp);
                else
                    hs.append(stmp);
            }
            return hs.toString();
        }

        /**
         * 添加参数的封装方法
         * 
         * @param params
         * @param orgin
         * @return
         */
        private StringBuffer getBeforeSign(Map<String, Object> params, StringBuffer orgin) {
            if (params == null) {
                return null;
            }
            List<Entry<String, Object>> entryList = new ArrayList<Map.Entry<String, Object>>(params.entrySet());
            ascentOrder(entryList);
            for (Entry<String, Object> entry : entryList) {
                orgin.append(entry.getKey()).append(entry.getValue());
            }

            return orgin;
        }
    };

}
