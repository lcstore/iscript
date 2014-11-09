package com.lezo.iscript.yeam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
	public void testFillUnionUrls() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/pmsg.txt"), "UTF-8");
		BatchIterator<String> it = new BatchIterator<String>(lineList, 500);
		String accessToken = "482f64db-bf61-42a7-a250-f9b1e786d00b";
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
