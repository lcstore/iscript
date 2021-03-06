package com.lezo.rest.jos;

import org.json.JSONObject;
import org.junit.Test;

public class JosRestClientTest {
    String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
    String appSecret = "7b7d95759e594b2f89a553b350f3d131";
    String accessToken = "83de1487-026f-4a60-8dac-a9dd27abfeae";
    @Test
    public void testGetWares() throws Exception {
        JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
        String method = "360buy.ware.get";
        JSONObject argsObject = new JSONObject();
        argsObject.put("ware_id", "341833");
        argsObject.put("fields", "ware_id,spu_id,cid");
        String result = client.execute(method, argsObject.toString());
        System.out.println(result);
    }
	@Test
	public void testCateRequest() throws Exception {
		String accessToken = "";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.product.catelogy.list.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("catelogyId", 5021);
		argsObject.put("level", 3);
		argsObject.put("isIcon", true);
		argsObject.put("isDescription", true);
		argsObject.put("client", "m");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testCateProductList() throws Exception {
		String accessToken = "";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.promotion.search.catelogy.list";
		int page = 1;
		int pageSize = 10;
		String sCid = "5021";
		JSONObject argsObject = new JSONObject();
		argsObject.put("catelogyId", sCid);
		argsObject.put("page", page);
		argsObject.put("pageSize", pageSize);
		argsObject.put("client", "m");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testProduct() throws Exception {
		String accessToken = "";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.baseproduct.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("ids", "317652,735029");
        argsObject.put("base", "sku_id,name,state,ebrand,cbrand,upc_code");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testPrice() throws Exception {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.price.get";
		JSONObject argsObject = new JSONObject();
		// argsObject.put("skuId ", "735029");
		// argsObject.put("isLoadWareScore", "true");
		// argsObject.put("client", "m");
		argsObject.put("sku_id", "J_317652");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testPromot() throws Exception {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.promotionInfo.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("skuId", "1178714");
		argsObject.put("webSite", "1");
		argsObject.put("origin", "1");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testDetail() throws Exception {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.product.detail.search.list.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("skuId", "925982");
		argsObject.put("isLoadWareScore", "true");
		argsObject.put("client", "m");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testStock() throws Exception {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.product.stock.list.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("skuId", "317652");
		argsObject.put("provinceId", "1");
		argsObject.put("client", "m");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testSearch() throws Exception {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.product.search.list.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("isLoadAverageScore", "true");
		argsObject.put("isLoadPromotion", "true");
		argsObject.put("sort", "1");// 1:销量排序 2:价格降序 3:价格升序 4:好评度 6:评论数
		argsObject.put("page", "1");
		argsObject.put("pageSize", "50");
		argsObject.put("keyword", "巧克力");
		argsObject.put("client", "m");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testProvince() throws Exception {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.selected.province.list.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("client", "m");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testWebPromotion() throws Exception {
		// 网盟推广
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.service.promotion.getcode";
		method = "jingdong.service.promotion.batch.getcode";
		JSONObject argsObject = new JSONObject();
        String pCode = "1530766875";
		argsObject.put("id", pCode);
        argsObject.put("url", "http://item.jd.com/" + pCode + ".html");
		argsObject.put("unionId", "51698052");
		argsObject.put("channel", "PC");
		argsObject.put("subUnionId", "");
        argsObject.put("webId", "220524281");
		argsObject.put("ext1", "");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
		System.out.println(result.length());
	}

	@Test
	public void testPromotionGetCodes() throws Exception {
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.service.promotion.getcode";
        method = "jingdong.service.promotion.batch.getcode";
		JSONObject argsObject = new JSONObject();
		argsObject.put("id", "1322501291");
		argsObject.put("url", "http://item.jd.com/1322501291.html");
		argsObject.put("unionId", "51698052");
		argsObject.put("channel", "PC");
		argsObject.put("subUnionId", "");
        argsObject.put("webId", "220524281");
		argsObject.put("ext1", "");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

}
