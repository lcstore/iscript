package com.lezo.rest.jos;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lezo.rest.SignBuildable;
import com.lezo.rest.jos.JosSignBuilder;

public class JosRestTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String serverurl = "http://gw.api.360buy.com/routerjson";
		String method = "jingdong.union.promotionshop.update";
		method = "jingdong.ware.product.catelogy.list.get";
		method = "jingdong.ware.promotion.search.catelogy.list";
		Map<String, Object> inMap = getRestParam(method);
		SignBuildable builder = new JosSignBuilder();
		String sign = builder.getSign(inMap);
		System.out.println(sign);
		inMap.put("sign", sign);
		inMap.remove("app_secret");
		// Get请求
		HttpGet httpget = new HttpGet(serverurl);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		for (Entry<String, Object> entry : inMap.entrySet()) {
			paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
		}
		DefaultHttpClient hc = new DefaultHttpClient();
		String params = EntityUtils.toString(new UrlEncodedFormEntity(paramList));
		httpget.setURI(new URI(httpget.getURI().toString() + "?" + params));
		HttpResponse httpresponse = hc.execute(httpget);
		HttpEntity entity = httpresponse.getEntity();
		System.out.println(EntityUtils.toString(entity));
	}

	private static Map<String, Object> getCommonMap() {
		String accessToken = "";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "1503e8459a4b4592a281334c311e6ced";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(new Date());
		Map<String, Object> inMap = new HashMap<String, Object>();
		inMap.put("access_token", accessToken);
		inMap.put("app_key", appKey);
		inMap.put("app_secret", appSecret);
		inMap.put("format", "json");
		inMap.put("timestamp", timestamp);
		inMap.put("v", "2.0");
		return inMap;
	}

	private static Map<String, Object> getRestParam(String method) {
		Map<String, Object> inMap = getCommonMap();
		inMap.put("method", method);
		Map<String, Object> clientMap = new HashMap<String, Object>();
		clientMap.put("catelogyId", 1);
		clientMap.put("level", 1);
		clientMap.put("isIcon", true);
		clientMap.put("isDescription", true);
		clientMap.put("client", "m");

		JSONObject paramObject = new JSONObject(clientMap);
		inMap.put("360buy_param_json", paramObject.toString());
		// try {
		// inMap.put("360buy_param_json", getParamJson());
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		return inMap;
	}

	private static String getParamJson() throws JSONException {
		JSONObject jObj = new JSONObject();
		JSONArray idArray = new JSONArray();
		idArray.put("1011457975");
		idArray.put("897738");
		jObj.put("ids", idArray);
		JSONArray baseArray = new JSONArray();
		baseArray.put("brandName");
		baseArray.put("name");
		jObj.put("base", baseArray);
		return jObj.toString();
	}
}
