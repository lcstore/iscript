package com.lezo.iscript.yeam.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class PullJdListTimer {
	private static Logger log = Logger.getLogger(PullJdListTimer.class);
	private static volatile boolean running = false;
	private static final JDCid2PList jdCid2PList = new JDCid2PList();

	public void run() {
		if (running) {
			log.warn("PullJdListTimer is working...");
			return;
		}
		try {
			running = true;
			doPull();
		} catch (Exception ex) {
			log.warn("", ex);
			ex.fillInStackTrace();
		} finally {
			running = false;
		}
	}

	private void doPull() {
		TaskWritable task = new TaskWritable();
		task.put("catelogyId", 5021);
		while (true) {
			String rs = doRetryCall(task);
			if (StringUtils.isEmpty(rs)) {
				break;
			}
			JSONObject rsObject = JSONUtils.getJSONObject(rs);
			JSONObject nextObject = JSONUtils.get(rsObject, "next");
			if (nextObject == null) {
				break;
			} else {
				Integer page = JSONUtils.getInteger(nextObject, "page");
				task.put("page", page);
				log.info("Next:" + page + "," + JSONUtils.get(rsObject, "oHeader"));
			}
			handleResult(rsObject);
			break;
		}

	}

	private void handleResult(JSONObject rsObject) {
		JSONArray oList = JSONUtils.get(rsObject, "oList");
		if (oList == null) {
			return;
		}

		System.out.println(oList);
		List<ProductDto> productDtos = new ArrayList<ProductDto>();
		List<ProductStatDto> productStatDtos = new ArrayList<ProductStatDto>();
		for (int i = 0; i < oList.length(); i++) {
			try {
				JSONObject itemObject = oList.getJSONObject(i);
				handleItem(itemObject, productDtos, productStatDtos);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleItem(JSONObject itemObject, List<ProductDto> productDtos, List<ProductStatDto> productStatDtos) {
		ProductDto productDto = new ProductDto();
		productDto.setProductCode(JSONUtils.getString(itemObject, "skuId"));
		if (StringUtils.isEmpty(productDto.getProductCode())) {
			return;
		}
		productDto.setSiteCode("jd.com");
		productDto.setImgUrl(JSONUtils.getString(itemObject, "imageUrl"));
		productDto.setProductName(JSONUtils.getString(itemObject, "wareName"));
		productDto.setMarketPrice(JSONUtils.getFloat(itemObject, "martPrice"));
		productDto.setProductUrl(String.format("http://item.jd.com/%.html", productDto.getProductCode()));
		productDto.setCreateTime(new Date());
		productDto.setUpdateTime(productDto.getCreateTime());

		ProductStatDto statDto = new ProductStatDto();
		statDto.setProductCode(productDto.getProductCode());
		statDto.setProductName(productDto.getProductName());
		statDto.setProductPrice(JSONUtils.getFloat(itemObject, "jdPrice"));
		statDto.setCreateTime(productDto.getCreateTime());
		statDto.setUpdateTime(productDto.getUpdateTime());

		productDtos.add(productDto);
		productStatDtos.add(statDto);
	}

	private String doRetryCall(TaskWritable task) {
		int retry = 0;
		while (++retry < 3) {
			try {
				return jdCid2PList.doParse(task);
			} catch (Exception e) {
				retry++;
				log.info("retry:" + retry + ",args:" + new JSONObject(task.getArgs()));
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}
}
