package com.lezo.iscript.yeam.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.ProductStatHisService;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class PullJdListTimer {
	private static Logger log = Logger.getLogger(PullJdListTimer.class);
	private static volatile boolean running = false;
	private static final JDCid2PList jdCid2PList = new JDCid2PList();
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductStatService productStatService;
	@Autowired
	private ProductStatHisService productStatHisService;

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
		}

	}

	private void handleResult(JSONObject rsObject) {
		JSONArray oList = JSONUtils.get(rsObject, "oList");
		if (oList == null) {
			return;
		}
		for (int i = 0; i < oList.length(); i++) {
			try {
				List<ProductDto> productDtos = new ArrayList<ProductDto>();
				List<ProductStatDto> productStatDtos = new ArrayList<ProductStatDto>();
				JSONObject itemObject = oList.getJSONObject(i);
				handleItem(itemObject, productDtos, productStatDtos);
				handleDtos(productDtos, productStatDtos);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private void handleDtos(List<ProductDto> productDtos, List<ProductStatDto> productStatDtos) {
		List<ProductDto> insertDtos = new ArrayList<ProductDto>();
		List<ProductDto> updateDtos = new ArrayList<ProductDto>();
		List<ProductStatDto> insertStatDtos = new ArrayList<ProductStatDto>();
		List<ProductStatDto> insertStatHisDtos = new ArrayList<ProductStatDto>();
		List<ProductStatDto> updateStatDtos = new ArrayList<ProductStatDto>();
		doAssort(productDtos, insertDtos, updateDtos);
		doStatAssort(productStatDtos, insertStatDtos, updateStatDtos, insertStatHisDtos);
		productService.batchInsertProductDtos(insertDtos);
		productService.batchUpdateProductDtos(updateDtos);
		productStatService.batchInsertProductStatDtos(insertStatDtos);
		productStatService.batchUpdateProductStatDtos(updateStatDtos);
		turnCreateTime2UpdateTime(insertStatHisDtos);
		productStatHisService.batchInsertProductStatHisDtos(insertStatHisDtos);
	}

	private void turnCreateTime2UpdateTime(List<ProductStatDto> insertStatHisDtos) {
		for (ProductStatDto hisDto : insertStatHisDtos) {
			hisDto.setCreateTime(hisDto.getUpdateTime());
		}
	}

	private void doAssort(List<ProductDto> productDtos, List<ProductDto> insertDtos, List<ProductDto> updateDtos) {
		Map<Integer, Set<String>> shopMap = new HashMap<Integer, Set<String>>();
		Map<String, ProductDto> dtoMap = new HashMap<String, ProductDto>();
		for (ProductDto dto : productDtos) {
			String key = dto.getShopId() + "-" + dto.getProductCode();
			dtoMap.put(key, dto);
			Set<String> codeSet = shopMap.get(dto.getShopId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				shopMap.put(dto.getShopId(), codeSet);
			}
			codeSet.add(dto.getProductCode());
		}
		for (Entry<Integer, Set<String>> entry : shopMap.entrySet()) {
			List<ProductDto> hasDtos = productService.getProductDtos(new ArrayList<String>(entry.getValue()), entry.getKey());
			Set<String> hasCodeSet = new HashSet<String>();
			for (ProductDto dto : hasDtos) {
				String key = dto.getShopId() + "-" + dto.getProductCode();
				ProductDto newDto = dtoMap.get(key);
				hasCodeSet.add(dto.getProductCode());
				newDto.setId(dto.getId());
				updateDtos.add(newDto);
			}
			for (String code : entry.getValue()) {
				if (hasCodeSet.contains(code)) {
					continue;
				}
				String key = entry.getKey() + "-" + code;
				ProductDto newDto = dtoMap.get(key);
				insertDtos.add(newDto);
			}

		}

	}

	private void doStatAssort(List<ProductStatDto> productDtos, List<ProductStatDto> insertDtos, List<ProductStatDto> updateDtos, List<ProductStatDto> insertStatHisDtos) {
		Map<Integer, Set<String>> shopMap = new HashMap<Integer, Set<String>>();
		Map<String, ProductStatDto> dtoMap = new HashMap<String, ProductStatDto>();
		for (ProductStatDto dto : productDtos) {
			String key = dto.getShopId() + "-" + dto.getProductCode();
			dtoMap.put(key, dto);
			Set<String> codeSet = shopMap.get(dto.getShopId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				shopMap.put(dto.getShopId(), codeSet);
			}
			codeSet.add(dto.getProductCode());
		}
		for (Entry<Integer, Set<String>> entry : shopMap.entrySet()) {
			List<ProductStatDto> hasDtos = productStatService.getProductStatDtos(new ArrayList<String>(entry.getValue()), entry.getKey(), null);
			Set<String> hasCodeSet = new HashSet<String>();
			for (ProductStatDto oldDto : hasDtos) {
				String key = oldDto.getShopId() + "-" + oldDto.getProductCode();
				ProductStatDto newDto = dtoMap.get(key);
				hasCodeSet.add(oldDto.getProductCode());
				newDto.setId(oldDto.getId());
				updateDtos.add(newDto);
				if (isChange(oldDto, newDto)) {
					insertStatHisDtos.add(newDto);
				}
			}
			for (String code : entry.getValue()) {
				if (hasCodeSet.contains(code)) {
					continue;
				}
				String key = entry.getKey() + "-" + code;
				ProductStatDto newDto = dtoMap.get(key);
				insertDtos.add(newDto);
				insertStatHisDtos.add(newDto);
			}

		}

	}

	private void handleItem(JSONObject itemObject, List<ProductDto> productDtos, List<ProductStatDto> productStatDtos) {
		ProductDto productDto = new ProductDto();
		productDto.setProductCode(JSONUtils.getString(itemObject, "skuId"));
		if (StringUtils.isEmpty(productDto.getProductCode())) {
			return;
		}
		productDto.setImgUrl(JSONUtils.getString(itemObject, "imageUrl"));
		productDto.setProductName(JSONUtils.getString(itemObject, "wareName"));
		productDto.setMarketPrice(JSONUtils.getFloat(itemObject, "martPrice"));
		productDto.setProductUrl(String.format("http://item.jd.com/%s.html", productDto.getProductCode()));
		productDto.setCreateTime(new Date());
		productDto.setUpdateTime(productDto.getCreateTime());
		ShopDto dto = ShopCacher.getInstance().insertIfAbsent("京东商城", "http://www.jd.com/", null);
		if (dto != null) {
			productDto.setShopId(dto.getId());
		}

		ProductStatDto statDto = new ProductStatDto();
		statDto.setProductCode(productDto.getProductCode());
		statDto.setProductName(productDto.getProductName());
		statDto.setProductUrl(productDto.getProductUrl());
		statDto.setProductPrice(JSONUtils.getFloat(itemObject, "jdPrice"));
		statDto.setMarketPrice(productDto.getMarketPrice());
		statDto.setCreateTime(productDto.getCreateTime());
		statDto.setUpdateTime(productDto.getUpdateTime());
		statDto.setShopId(1001);

		productDtos.add(productDto);
		productStatDtos.add(statDto);
	}

	public boolean isChange(ProductStatDto oldDto, ProductStatDto newDto) {
		if (!isSameObject(oldDto.getProductPrice(), newDto.getProductPrice())) {
			return true;
		}
		if (!isSameObject(oldDto.getMarketPrice(), newDto.getMarketPrice())) {
			return true;
		}
		if (!isSameObject(oldDto.getSoldNum(), newDto.getSoldNum())) {
			return true;
		}
		if (!isSameObject(oldDto.getStockNum(), newDto.getStockNum())) {
			return true;
		}
		if (!isSameObject(oldDto.getCommentNum(), newDto.getCommentNum())) {
			return true;
		}
		return false;
	}

	public boolean isSameObject(Object lObject, Object rObject) {
		if (lObject == null && rObject == null) {
			return true;
		} else if (lObject == null && rObject != null) {
			return false;
		}
		return lObject.equals(rObject);
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
