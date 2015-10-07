package com.lezo.rest.yhd;

import java.util.Map;

import org.junit.Test;

public class YhdRestClientTest {

    private String appKey = "10210015092300003589";
    private String appSecret = "ebf4272cc568ac51f13aa52c852e97d2";
    private String accessToken;
    private String trackerU = "103663742";
    private String serverUrl = "http://openapi.yhd.com/app/api/rest/router";
    String callBackUrl = "http://www.lezomao.com";

    @Test
    public void testGetCodeUrl() {
        String url =
                "https://member.yhd.com/login/authorize.do?client_id=" + appKey
                        + "&response_type=code&redirect_uri=" + callBackUrl;
        System.err.println(url);
    }

    @Test
    public void testGetToeknUrl() {
        String code = "4609";
        String url =
                "https://member.yhd.com/login/token.do?client_id=" + appKey + "&client_secret=" + appSecret
                        + "&code=" + code + "&grant_type=authorization_code&redirect_uri="
                        + callBackUrl;
        System.err.println(url);
    }

    @Test
    public void testUnionProductGet() throws Exception {
        String method = "yhd.union.single.product.get";
        YhdRestClient restClient = new YhdRestClient(appKey, appSecret, accessToken);
        Map<String, Object> paramMap = restClient.createSystemParaMap();
        paramMap.put("method", method);
        paramMap.put("trackerU", trackerU);
        paramMap.put("pmInfoId", "34161026");
        paramMap.put("app_secret", appSecret);
        String result = restClient.execute(serverUrl, paramMap);
        System.err.println(result);
    }

    // @Test
    // public void testSerialProductGet() throws Exception {
    // String method = "yhd.serial.product.get";
    // YhdRestClient restClient = new YhdRestClient(appKey, appSecret, accessToken);
    // Map<String, Object> paramMap = restClient.createSystemParaMap();
    // paramMap.put("method", method);
    // paramMap.put("productId", "70262");
    // String result = restClient.execute(serverUrl, paramMap);
    // System.err.println(result);
    // }

    // @Test
    // public void testUnionProductGetByClient() throws Exception {
    // String method = "yhd.union.single.product.get";
    // YhdClient yhd = new YhdClient(serverUrl, appKey, appSecret);
    // UnionSingleProductGetRequest request = new UnionSingleProductGetRequest();
    // request.setPmInfoId(34161026L);
    // request.setTrackerU(103663742L);
    // UnionSingleProductGetResponse response = yhd.excute(request);
    // System.err.println(response.getBody());
    // }

}
