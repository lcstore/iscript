package com.lezo.rest.jos;

import org.json.JSONObject;
import org.junit.Test;

import com.lezo.rest.jos.JosRestClient;

public class JosRestClientTest {

	@Test
	public void testCateRequest() throws Exception {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "1503e8459a4b4592a281334c311e6ced";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.product.catelogy.list.get";
		JSONObject argsObject = new JSONObject();
		argsObject.put("catelogyId", 9931);
		argsObject.put("level", 2);
		argsObject.put("isIcon", true);
		argsObject.put("isDescription", true);
		argsObject.put("client", "m");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

	@Test
	public void testCateProductList() throws Exception {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "1503e8459a4b4592a281334c311e6ced";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.ware.promotion.search.catelogy.list";
		int page = 1;
		int pageSize = 10;
		String sCid = "1348";
		JSONObject argsObject = new JSONObject();
		argsObject.put("catelogyId", sCid);
		argsObject.put("page", page);
		argsObject.put("pageSize", pageSize);
		argsObject.put("client", "m");
		String result = client.execute(method, argsObject.toString());
		System.out.println(result);
	}

}
