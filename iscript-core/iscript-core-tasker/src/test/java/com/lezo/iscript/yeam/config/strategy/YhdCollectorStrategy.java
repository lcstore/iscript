package com.lezo.iscript.yeam.config.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.common.storage.StorageTimeTrigger;
import com.lezo.iscript.service.crawler.dto.ListRankDto;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ListRankService;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.storage.StorageCaller;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class YhdCollectorStrategy implements ResultStrategy {
	private static Logger logger = LoggerFactory.getLogger(YhdCollectorStrategy.class);

	static {
		StorageTimeTrigger storageTimeTrigger = SpringBeanUtils.getBean(StorageTimeTrigger.class);
		storageTimeTrigger.addListener(ListRankDto.class, new StorageListener<ListRankDto>() {
			@Override
			public void doStorage() {
				logger.info("start ProxyDetectDto...");
				StorageBuffer<ListRankDto> storageBuffer = StorageBufferFactory.getStorageBuffer(ListRankDto.class);
				final List<ListRankDto> copyList = storageBuffer.moveTo();
				if (CollectionUtils.isEmpty(copyList)) {
					logger.info("insert ProxyDetectDto:0");
					return;
				}

				StorageCaller.getInstance().execute(new Runnable() {
					@Override
					public void run() {
						ListRankService listRankService = SpringBeanUtils.getBean(ListRankService.class);
						listRankService.batchSaveDtos(copyList);
					}
				});
			}
		});
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_FAIL == rWritable.getStatus()) {
			addRetry(rWritable);
		} else if (ResultWritable.RESULT_SUCCESS == rWritable.getStatus()) {
			if ("ConfigYhdCategory".equals(rWritable.getType())) {
				JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
				JSONObject argsObject = JSONUtils.get(jObject, "args");
				String rsString = JSONUtils.getString(jObject, "rs");
				try {
					JSONArray rootArray = new JSONArray(rsString);
					for (int i = 0; i < rootArray.length(); i++) {
						addListTasks(rootArray.getJSONObject(i), argsObject);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("ConfigYhdList".equals(rWritable.getType())) {
				JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
				JSONObject argsObject = JSONUtils.get(jObject, "args");
				String rsString = JSONUtils.getString(jObject, "rs");
				try {
					JSONObject rootObject = new JSONObject(rsString);
					addResults(rootObject, argsObject);
					addNextTasks(rootObject, argsObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("ConfigYhdProduct".equals(rWritable.getType())) {
				JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
				String rsString = JSONUtils.getString(jObject, "rs");
				try {
					JSONObject rootObject = new JSONObject(rsString);
					List<ProductDto> productDtos = new ArrayList<ProductDto>();
					List<ProductStatDto> productStatDtos = new ArrayList<ProductStatDto>();
					handleOne(rootObject, productDtos, productStatDtos);
					getStorageBuffer(ProductStatDto.class).addAll(productStatDtos);
					getStorageBuffer(ProductDto.class).addAll(productDtos);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void addListTasks(JSONObject cObject, JSONObject argsObject) throws Exception {
		if (cObject == null) {
			return;
		}
		JSONArray cArray = JSONUtils.get(cObject, "children");
		if (cArray == null || cArray.length() < 1) {
			String url = JSONUtils.getString(cObject, "url");
			JSONUtils.put(argsObject, "name", JSONUtils.getString(cObject, "name"));
			TaskPriorityDto taskDto = createPriorityDto(url, "ConfigYhdList", argsObject);
			getTaskPriorityDtoBuffer().add(taskDto);
		} else {
			for (int i = 0; i < cArray.length(); i++) {
				addListTasks(cArray.getJSONObject(i), argsObject);
			}
		}
	}

	private void addNextTasks(JSONObject rootObject, JSONObject argsObject) throws Exception {
		JSONArray nextArray = JSONUtils.get(rootObject, "nexts");
		if (nextArray == null) {
			return;
		}
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
		argsObject.remove("getNexts");
		for (int i = 0; i < nextArray.length(); i++) {
			String nextUrl = nextArray.getString(i);
			TaskPriorityDto taskPriorityDto = createPriorityDto(nextUrl, "ConfigYhdList", argsObject);
			taskPriorityDto.setParams(argsObject.toString());
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);
	}

	private TaskPriorityDto createPriorityDto(String url, String type, JSONObject argsObject) {
		TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
		taskPriorityDto.setBatchId(JSONUtils.getString(argsObject, "bid"));
		taskPriorityDto.setType(type);
		taskPriorityDto.setUrl(url);
		taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
		taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
		taskPriorityDto.setCreatTime(new Date());
		taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
		taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
		argsObject.remove("bid");
		argsObject.remove("type");
		argsObject.remove("url");
		argsObject.remove("level");
		argsObject.remove("src");
		argsObject.remove("ctime");
		if (taskPriorityDto.getLevel() == null) {
			taskPriorityDto.setLevel(0);
		}
		taskPriorityDto.setParams(argsObject.toString());
		return taskPriorityDto;
	}

	private void addResults(JSONObject rootObject, JSONObject argsObject) throws Exception {
		JSONArray listArray = JSONUtils.get(rootObject, "list");
		if (listArray == null) {
			return;
		}
		List<ProductDto> productDtos = new ArrayList<ProductDto>();
		List<ProductStatDto> productStatDtos = new ArrayList<ProductStatDto>();
		List<ListRankDto> listRankDtos = new ArrayList<ListRankDto>();
		for (int i = 0; i < listArray.length(); i++) {
			JSONObject itemObject = listArray.getJSONObject(i);
			JSONUtils.put(itemObject, "productUrl", JSONUtils.getObject(itemObject, "url"));
			handleOne(itemObject, productDtos, productStatDtos);
			createListRankDto(itemObject, argsObject, listRankDtos);
		}
		getStorageBuffer(ProductStatDto.class).addAll(productStatDtos);
		getStorageBuffer(ListRankDto.class).addAll(listRankDtos);

		// List<ProductDto> insertDtos = new ArrayList<ProductDto>();
		// List<ProductDto> updateDtos = new ArrayList<ProductDto>();
		// doAssort(productDtos, insertDtos, updateDtos);
		getStorageBuffer(ProductDto.class).addAll(productDtos);

		createProductTasks(argsObject, productDtos);

	}

	private void createListRankDto(JSONObject itemObject, JSONObject argsObject, List<ListRankDto> listRankDtos) {
		ListRankDto listRankDto = new ListRankDto();
		listRankDto.setProductCode(JSONUtils.getString(itemObject, "productCode"));
		if (StringUtils.isEmpty(listRankDto.getProductCode())) {
			return;
		}
		listRankDto.setProductUrl(JSONUtils.getString(itemObject, "productUrl"));
		ShopDto dto = ShopCacher.getInstance().getDomainShopDto(listRankDto.getProductUrl());
		if (dto != null) {
			listRankDto.setShopId(dto.getId());
		}
		listRankDto.setProductName(JSONUtils.getString(itemObject, "productName"));
		listRankDto.setCreateTime(new Date());
		listRankDto.setUpdateTime(listRankDto.getCreateTime());
		listRankDto.setListUrl(JSONUtils.getString(itemObject, "url"));
		listRankDto.setCategoryName(JSONUtils.getString(itemObject, "name"));
		listRankDto.setProductPrice(JSONUtils.getFloat(itemObject, "productPrice"));
		listRankDto.setSortRank(JSONUtils.getInteger(itemObject, "ranking"));
		listRankDto.setSortType(JSONUtils.getInteger(itemObject, "sortType"));
		listRankDtos.add(listRankDto);
	}

	private void createProductTasks(JSONObject argsObject, List<ProductDto> insertDtos) {
		if (insertDtos == null) {
			return;
		}
		String taskId = JSONUtils.getString(argsObject, "url");
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
		JSONUtils.put(argsObject, "strategy", getName());
		for (ProductDto dto : insertDtos) {
			String nextUrl = dto.getProductUrl();
			TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
			taskPriorityDto.setBatchId(taskId);
			taskPriorityDto.setType("ConfigYhdProduct");
			taskPriorityDto.setUrl(nextUrl);
			taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
			taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
			taskPriorityDto.setCreatTime(new Date());
			taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
			taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
			argsObject.remove("bid");
			argsObject.remove("type");
			argsObject.remove("url");
			argsObject.remove("level");
			argsObject.remove("src");
			argsObject.remove("ctime");
			if (taskPriorityDto.getLevel() == null) {
				taskPriorityDto.setLevel(0);
			}
			JSONUtils.put(argsObject, "pcode", dto.getProductCode());
			taskPriorityDto.setParams(argsObject.toString());
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);
	}

	private void doAssort(List<ProductDto> productDtos, List<ProductDto> insertDtos, List<ProductDto> updateDtos) {
		ProductService productService = SpringBeanUtils.getBean(ProductService.class);
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
			List<ProductDto> hasDtos = productService.getProductDtos(new ArrayList<String>(entry.getValue()),
					entry.getKey());
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

	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	public <T> StorageBuffer<T> getStorageBuffer(Class<T> dtoClass) {
		return StorageBufferFactory.getStorageBuffer(dtoClass);
	}

	private void addRetry(ResultWritable rWritable) {
		JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
		JSONObject argsObject = JSONUtils.get(jObject, "args");
		JSONObject exObject = JSONUtils.get(jObject, "ex");
		// String rsString = JSONUtils.getString(jObject, "rs");
		// JSONObject rsObject = JSONUtils.getJSONObject(rsString);
		TaskWritable tWritable = new TaskWritable();
		tWritable.setId(rWritable.getTaskId());
		Iterator<?> it = argsObject.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			tWritable.put(key, JSONUtils.getObject(argsObject, key));
		}
		Integer retry = (Integer) tWritable.get("retry");
		if (retry == null) {
			retry = 0;
		} else if (retry >= 3) {
			return;
		}
		tWritable.put("retry", retry + 1);
		Integer level = JSONUtils.getInteger(argsObject, "level");
		level = level == null ? 0 : level;
		TaskCacher.getInstance().getQueue(rWritable.getType()).offer(tWritable, level);
		logger.warn("retry task:" + tWritable.getId() + ",args:" + new JSONObject(tWritable.getArgs()) + ",ex:"
				+ exObject);
	}

	private void handleOne(JSONObject itemObject, List<ProductDto> productDtos, List<ProductStatDto> productStatDtos) {
		ProductDto productDto = new ProductDto();
		productDto.setProductCode(JSONUtils.getString(itemObject, "productCode"));
		if (StringUtils.isEmpty(productDto.getProductCode())) {
			return;
		}
		productDto.setProductUrl(JSONUtils.getString(itemObject, "productUrl"));
		ShopDto dto = ShopCacher.getInstance().getDomainShopDto(productDto.getProductUrl());
		if (dto != null) {
			productDto.setShopId(dto.getId());
		}
		productDto.setImgUrl(JSONUtils.getString(itemObject, "imgUrl"));
		productDto.setProductName(JSONUtils.getString(itemObject, "productName"));
		productDto.setMarketPrice(JSONUtils.getFloat(itemObject, "marketPrice"));
		productDto.setCreateTime(new Date());
		productDto.setUpdateTime(productDto.getCreateTime());
		productDto.setProductBrand(JSONUtils.getString(itemObject, "productBrand"));
		productDto.setProductModel(JSONUtils.getString(itemObject, "productModel"));
		Date onsailTime = JSONUtils.get(itemObject, "onsailTime");
		productDto.setOnsailTime(onsailTime);
		JSONObject attrObject = JSONUtils.get(itemObject, "attrs");
		String productAttr = attrObject == null ? null : attrObject.toString();
		productDto.setProductAttr(productAttr);

		ProductStatDto statDto = new ProductStatDto();
		statDto.setProductCode(productDto.getProductCode());
		statDto.setProductName(productDto.getProductName());
		statDto.setProductUrl(productDto.getProductUrl());
		statDto.setProductPrice(JSONUtils.getFloat(itemObject, "productPrice"));
		statDto.setMarketPrice(productDto.getMarketPrice());
		statDto.setCreateTime(productDto.getCreateTime());
		statDto.setUpdateTime(productDto.getUpdateTime());
		statDto.setShopId(productDto.getShopId());

		statDto.setCommentNum(JSONUtils.getInteger(itemObject, "commentNum"));
		statDto.setStockNum(JSONUtils.getInteger(itemObject, "stockNum"));
		statDto.setSoldNum(JSONUtils.getInteger(itemObject, "soldNum"));
		statDto.setCategoryNav(JSONUtils.getString(itemObject, "category_nav"));

		productDtos.add(productDto);
		productStatDtos.add(statDto);
	}
}
