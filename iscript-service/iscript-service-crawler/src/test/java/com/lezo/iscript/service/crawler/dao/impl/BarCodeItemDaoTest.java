package com.lezo.iscript.service.crawler.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.BarCodeItemDao;
import com.lezo.iscript.service.crawler.dao.ShopDao;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BarCodeUtils;
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
		List<BarCodeItemDto> dtoList = barCodeItemDao.getBarCodeItemDtos(barCodeList);
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
		List<BarCodeItemDto> hasDtos = barCodeItemDao.getBarCodeItemDtos(new ArrayList<String>(dtoMap.keySet()));
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
		ShopDao shopInfoDao = SpringBeanUtils.getBean(ShopDao.class);
		String file = "D:/ancc-p1/result-1001.log";
		Reader in = new FileReader(file);
		BufferedReader bReader = new BufferedReader(in);
		int count = 0;
		Map<String, ShopDto> shopMap = new HashMap<String, ShopDto>();
		while (bReader.ready()) {
			String line = bReader.readLine();
			if (line == null) {
				break;
			}
			Map<String, ShopDto> dtoMap = createShopInfoDto(line);
			if (dtoMap.isEmpty()) {
				continue;
			}
			count++;
			shopMap.putAll(dtoMap);
			if (shopMap.size() >= 200) {
				shopInfoDao.batchInsert(new ArrayList<ShopDto>(shopMap.values()));
				shopMap.clear();
			}
		}
		if (!shopMap.isEmpty()) {
			shopInfoDao.batchInsert(new ArrayList<ShopDto>(shopMap.values()));
		}
		IOUtils.closeQuietly(bReader);
		System.out.println("total.count:" + count);
	}

	private Map<String, ShopDto> createShopInfoDto(String line) {
		int index = line.indexOf("{");
		Map<String, ShopDto> dtoMap = new HashMap<String, ShopDto>();
		if (index < 0) {
			return dtoMap;
		}
		JSONObject jsonObject = JSONUtils.getJSONObject(line.substring(index));
		JSONArray itemArray = JSONUtils.get(jsonObject, "items");
		for (int i = 0; i < itemArray.length(); i++) {
			try {
				JSONObject itemObject = itemArray.getJSONObject(i);
				String barCode = JSONUtils.getString(itemObject, "pBarCode");
				ShopDto dto = new ShopDto();
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

	@Test
	public void testDeleteFromId() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);
		List<Long> idList = new ArrayList<Long>();
		idList.add(208L);
		Integer count = barCodeItemDao.deleteFromId(idList);
		System.err.println("Delete:" + count);
	}

	@Test
	public void testDeleteFromIds() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);
		List<String> barCodeList = new ArrayList<String>();
		barCodeList.add("0800050004787");
		barCodeList.add("8801062276035");
		List<BarCodeItemDto> dtoList = barCodeItemDao.getBarCodeItemDtos(barCodeList);
		Map<String, Set<Long>> codeIdMap = new HashMap<String, Set<Long>>();
		for (BarCodeItemDto dto : dtoList) {
			Set<Long> idSet = codeIdMap.get(dto.getBarCode());
			if (idSet == null) {
				idSet = new HashSet<Long>();
				codeIdMap.put(dto.getBarCode(), idSet);
			}
			idSet.add(dto.getId());
		}
		for (Entry<String, Set<Long>> entry : codeIdMap.entrySet()) {
			List<Long> idList = new ArrayList<Long>(entry.getValue());
			idList = idList.subList(1, idList.size());
			barCodeItemDao.deleteFromId(idList);
		}
	}

	@Test
	public void testFilterBarCode() throws Exception {
		List<String> lineList = FileUtils.readLines(new File("C:/Users/lezo.li/Desktop/bcode-src.txt"), "UTF-8");
		Pattern oReg = Pattern.compile("[0-9]{13}");
		Set<String> codeSet = new HashSet<String>();
		for (String line : lineList) {
			String[] unitArr = line.split("\t");
			if (unitArr == null || unitArr.length < 2) {
				continue;
			}
			Matcher matcher = oReg.matcher(unitArr[0]);
			if (matcher.find() && BarCodeUtils.isBarCode(matcher.group())) {
				String barCode = matcher.group();
				if (!codeSet.contains(barCode)) {
					line = line.replace(unitArr[0], barCode);
					System.out.println(line);
					codeSet.add(barCode);
				}
			}
		}
		System.err.println("end:" + lineList.size());
	}
}
