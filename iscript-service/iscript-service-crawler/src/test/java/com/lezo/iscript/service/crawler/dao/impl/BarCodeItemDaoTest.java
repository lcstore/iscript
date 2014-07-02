package com.lezo.iscript.service.crawler.dao.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.BarCodeItemDao;
import com.lezo.iscript.service.crawler.dao.ShopInfoDao;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.dto.ShopInfoDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;

public class BarCodeItemDaoTest {

	@Test
	public void testBatchInsert() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);

		List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
		BarCodeItemDto dto = new BarCodeItemDto();
		dto.setBarCode("bc");
		dto.setCreateTime(new Date());
		dto.setImgUrl("imgUrl");
		dto.setProductAttr("productAttr");
		dto.setProductName("productName");
		dto.setProductUrl("productUrl");
		dto.setUpdateTime(new Date());
		dtoList.add(dto);
		barCodeItemDao.batchInsert(dtoList);
	}

	@Test
	public void testGetBarCodeItemDtos() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);

		List<String> barCodeList = new ArrayList<String>();
		barCodeList.add("bc");
		List<BarCodeItemDto> dtoList = barCodeItemDao.getBarCodeItemDtos(barCodeList, null);
		Assert.assertEquals(false, dtoList.isEmpty());
	}

	@Test
	public void testFileBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);
		String file = "D:/ancc-p1/result-1001.log";
		Reader in = new FileReader(file);
		BufferedReader bReader = new BufferedReader(in);
		int count = 0;
		List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
		while (bReader.ready()) {
			String line = bReader.readLine();
			if (line == null) {
				break;
			}
			List<BarCodeItemDto> itemDtos = createBarCodeItemDto(line);
			if (itemDtos.isEmpty()) {
				continue;
			}
			count++;
			dtoList.addAll(itemDtos);
			if (dtoList.size() >= 500) {
				saveItems(barCodeItemDao, dtoList);
				dtoList = new ArrayList<BarCodeItemDto>();
			}
		}
		if (!dtoList.isEmpty()) {
			saveItems(barCodeItemDao, dtoList);
		}
		IOUtils.closeQuietly(bReader);
		System.out.println("total.count:" + count);
	}

	private void saveItems(BarCodeItemDao barCodeItemDao, List<BarCodeItemDto> dtoList) {
		Map<String, BarCodeItemDto> dtoMap = new HashMap<String, BarCodeItemDto>();
		for (BarCodeItemDto dto : dtoList) {
			dtoMap.put(dto.getBarCode(), dto);
		}
		List<BarCodeItemDto> hasDtos = barCodeItemDao.getBarCodeItemDtos(new ArrayList<String>(dtoMap.keySet()), null);
		Map<String, BarCodeItemDto> hasMap = new HashMap<String, BarCodeItemDto>();
		for (BarCodeItemDto dto : hasDtos) {
			hasMap.put(dto.getBarCode(), dto);
		}
		List<BarCodeItemDto> insertDtos = new ArrayList<BarCodeItemDto>();
		for (Entry<String, BarCodeItemDto> entry : dtoMap.entrySet()) {
			if (!hasDtos.contains(entry.getKey())) {
				insertDtos.add(entry.getValue());
			}
		}
		barCodeItemDao.batchInsert(insertDtos);
		System.out.println("insert:" + insertDtos.size());
	}

	private List<BarCodeItemDto> createBarCodeItemDto(String line) {
		int index = line.indexOf("{");
		List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
		if (index < 0) {
			return dtoList;
		}
		JSONObject jsonObject = JSONUtils.getJSONObject(line.substring(index));
		JSONArray itemArray = JSONUtils.get(jsonObject, "items");
		for (int i = 0; i < itemArray.length(); i++) {
			try {
				JSONObject itemObject = itemArray.getJSONObject(i);
				BarCodeItemDto dto = new BarCodeItemDto();
				dto.setBarCode(JSONUtils.getString(itemObject, "pBarCode"));
				dto.setProductName(JSONUtils.getString(itemObject, "pName"));
				dto.setProductUrl(JSONUtils.getString(itemObject, "pUrl"));
				dto.setProductBrand(JSONUtils.getString(itemObject, "pBrand"));
				dto.setProductModel(JSONUtils.getString(itemObject, "pModel"));
				dto.setProductAttr(JSONUtils.getString(itemObject, "pText"));
				dto.setImgUrl(JSONUtils.getString(itemObject, "pImg"));
				dto.setCreateTime(new Date());
				dto.setUpdateTime(new Date());
				dtoList.add(dto);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return dtoList;
	}

	@Test
	public void testFileShopBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ShopInfoDao shopInfoDao = SpringBeanUtils.getBean(ShopInfoDao.class);
		String file = "D:/ancc-p1/result-1001.log";
		Reader in = new FileReader(file);
		BufferedReader bReader = new BufferedReader(in);
		int count = 0;
		Map<String, ShopInfoDto> shopMap = new HashMap<String, ShopInfoDto>();
		while (bReader.ready()) {
			String line = bReader.readLine();
			if (line == null) {
				break;
			}
			Map<String, ShopInfoDto> dtoMap = createShopInfoDto(line);
			if (dtoMap.isEmpty()) {
				continue;
			}
			count++;
			shopMap.putAll(dtoMap);
			if (shopMap.size() >= 200) {
				shopInfoDao.batchInsert(new ArrayList<ShopInfoDto>(shopMap.values()));
				shopMap.clear();
			}
		}
		if (!shopMap.isEmpty()) {
			shopInfoDao.batchInsert(new ArrayList<ShopInfoDto>(shopMap.values()));
		}
		IOUtils.closeQuietly(bReader);
		System.out.println("total.count:" + count);
	}

	private Map<String, ShopInfoDto> createShopInfoDto(String line) {
		int index = line.indexOf("{");
		Map<String, ShopInfoDto> dtoMap = new HashMap<String, ShopInfoDto>();
		if (index < 0) {
			return dtoMap;
		}
		JSONObject jsonObject = JSONUtils.getJSONObject(line.substring(index));
		JSONArray itemArray = JSONUtils.get(jsonObject, "items");
		for (int i = 0; i < itemArray.length(); i++) {
			try {
				JSONObject itemObject = itemArray.getJSONObject(i);
				String barCode = JSONUtils.getString(itemObject, "pBarCode");
				ShopInfoDto dto = new ShopInfoDto();
				dto.setShopCode(barCode.substring(0, 8));
				dto.setShopName(JSONUtils.getString(itemObject, "spName"));
				dto.setShopUrl(JSONUtils.getString(itemObject, "spUrl"));
				dto.setCreateTime(new Date());
				dto.setUpdateTime(new Date());
				dtoMap.put(dto.getShopCode(), dto);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return dtoMap;
	}
}
