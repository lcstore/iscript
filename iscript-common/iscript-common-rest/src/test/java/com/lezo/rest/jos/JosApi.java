package com.lezo.rest.jos;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class JosApi {
    String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
    String appSecret = "7b7d95759e594b2f89a553b350f3d131";
    String appUrl = "http://www.lezomao.com";

    @Test
    public void testJosToken() throws Exception {
        String code = "mHhL1C";
        StringBuilder sb = new StringBuilder();
        sb.append("https://auth.360buy.com/oauth/token?grant_type=authorization_code");
        sb.append("&client_id=" + appKey);
        sb.append("&client_secret=" + appSecret);
        sb.append("&scope=read&redirect_uri=" + appUrl);
        sb.append("&code=" + code);
        sb.append("&state=web");

        DefaultHttpClient client = new DefaultHttpClient();
        HttpProtocolParams.setUseExpectContinue(client.getParams(), false);

        HttpPost post = new HttpPost(sb.toString());
        post.setHeader("Accept-Charset", "utf-8");
        HttpResponse respone = client.execute(post);
        HttpEntity entity = respone.getEntity();
        System.out.println(EntityUtils.toString(entity));
    }

    @Test
    public void testJosCode() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("https://oauth.jd.com/oauth/authorize?response_type=code");
        sb.append("&client_id=" + appKey);
        sb.append("&redirect_uri=" + appUrl);
        sb.append("&state=web");

        System.out.println("url:\n" + sb.toString());
    }
}
