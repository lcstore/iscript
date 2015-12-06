package com.lezo;

import org.junit.Test;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemsGetRequest;
import com.taobao.api.response.TbkItemsGetResponse;

public class TbkApiTest {

    private String url = "http://gw.api.taobao.com/router/rest";
    private String appkey = "23250128";
    private String secret = "90513798acb93574b56a4abde91a7a5a";

    @Test
    public void testTbkItemsGetRequest() throws Exception {
        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        TbkItemsGetRequest req = new TbkItemsGetRequest();
        req.setFields("num_iid,seller_id,nick,title,volume,pic_url,item_url,shop_url");
        req.setKeyword("手机");
        // req.setCid(50024400L);
        req.setStartPrice("1");
        req.setEndPrice("10000");
        // req.setAutoSend("true");
        // req.setArea("杭州");
        // req.setStartCredit("3diamond");
        // req.setEndCredit("5goldencrown");
        // req.setSort("price_desc");
        // req.setGuarantee("true");
        req.setStartCommissionRate("1234");
        req.setEndCommissionRate("10000");
        req.setStartCommissionNum("1000");
        req.setEndCommissionNum("1000000");
        // req.setStartTotalnum("1");
        // req.setEndTotalnum("100000");
        // req.setCashCoupon("true");
        // req.setVipCard("true");
        // req.setOverseasItem("true");
        // req.setSevendaysReturn("true");
        // req.setRealDescribe("true");
        // req.setOnemonthRepair("true");
        // req.setCashOndelivery("true");
        // req.setMallItem("true");
        req.setPageNo(1L);
        req.setPageSize(40L);
        // req.setIsMobile(true);
        TbkItemsGetResponse rsp = client.execute(req);
        System.out.println(rsp.getBody());
    }
}
