package com.lezo.iscript.yeam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProductDao;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.rest.jos.JosRestClient;

public class FillUnionTest {

	@Test
	public void testToken() throws Exception {
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		String code = "";
		String directUrl = "http://www.lezomao.com";
		// https://auth.360buy.com/oauth/authorize?response_type=code&client_id=6BB6B1912DAB91E14B6ADF6C31A2C023&redirect_uri=http://www.lezomao.com
		String accessToken = getToken(appKey, appSecret, directUrl, code);
		System.err.println("token:" + accessToken);
	}

	private String getToken(String appKey, String appSecret, String directUrl, String code) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("https://auth.360buy.com/oauth/token?grant_type=authorization_code&client_id=");
		sb.append(appKey);
		sb.append("&redirect_uri=");
		sb.append(directUrl);
		sb.append("&code=GET_CODE&state=");
		sb.append(code);
		sb.append("&client_secret=");
		sb.append(appSecret);
		String tokenUrl = sb.toString();
		HttpGet get = new HttpGet(tokenUrl);
		HttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(get);
		String result = EntityUtils.toString(resp.getEntity(), "UTF-8");
		JSONObject rObject = JSONUtils.getJSONObject(result);
		return JSONUtils.getString(rObject, "access_token");
	}

	@Test
	public void testFillUnionUrls() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/pmsg.txt"), "UTF-8");

		BatchIterator<String> it = new BatchIterator<String>(lineList, 500);
		String accessToken = "83de1487-026f-4a60-8dac-a9dd27abfeae";
		String appKey = "6BB6B1912DAB91E14B6ADF6C31A2C023";
		String appSecret = "7b7d95759e594b2f89a553b350f3d131";
		JosRestClient client = new JosRestClient(appKey, appSecret, accessToken);
		String method = "jingdong.service.promotion.getcode";
		method = "jingdong.service.promotion.batch.getcode";
		JSONObject argsObject = new JSONObject();
		argsObject.put("unionId", "51698052");
		argsObject.put("channel", "PC");
		argsObject.put("subUnionId", "");
		argsObject.put("webId", "");
		argsObject.put("ext1", "");
		long start = System.currentTimeMillis();
		while (it.hasNext()) {
			List<ProductDto> unionDtoList = new ArrayList<ProductDto>();
			int index = 0;
			for (String line : it.next()) {
				String[] unitArr = line.split("\t");
				try {
					addUnion(unionDtoList, argsObject, unitArr[0].trim(), unitArr[1].trim(), client, method);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(line);
					TimeUnit.MILLISECONDS.sleep(1000);
				}
				index++;
				System.out.println("count:" + index);
			}
			if (!unionDtoList.isEmpty()) {
				productDao.batchUpdateUnionUrls(unionDtoList);
			}
		}
		long cost = System.currentTimeMillis() - start;
		System.err.println("line:" + lineList.size() + ",cost:" + cost);
	}

	private void addUnion(List<ProductDto> unionDtoList, JSONObject argsObject, String idString, String pCode, JosRestClient client, String method) throws Exception {
		argsObject.put("id", pCode);
		argsObject.put("url", "http://item.jd.com/" + pCode + ".html");
		String result = client.execute(method, argsObject.toString());
		JSONObject rsObject = JSONUtils.getJSONObject(result);
		// .jingdong_service_promotion_batch_getcode_responce
		rsObject = JSONUtils.getJSONObject(rsObject, "jingdong_service_promotion_batch_getcode_responce");
		if (rsObject == null) {
			System.err.println(result);
			return;
		}
		rsObject = JSONUtils.getJSONObject(rsObject, "querybatch_result");
		if (rsObject == null) {
			System.err.println(result);
			return;
		}
		JSONArray rsArray = JSONUtils.get(rsObject, "urlList");
		if (rsArray != null) {
			for (int i = 0; i < rsArray.length(); i++) {
				rsObject = rsArray.getJSONObject(i);
				String unionUrl = JSONUtils.getString(rsObject, "url");
				if (!StringUtils.isEmpty(unionUrl)) {
					ProductDto dto = new ProductDto();
					dto.setId(Long.valueOf(idString));
					dto.setUnionUrl(unionUrl);
					unionDtoList.add(dto);
					System.out.println(rsObject);
					break;
				}
			}
		}
	}
}
