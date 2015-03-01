package com.lezo.iscript.service.crawler.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProductDao;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BatchIterator;

public class ProductDaoTest {

	@Test
	public void testBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<ProductDto> dtoList = new ArrayList<ProductDto>();
		ProductDto dto = new ProductDto();
		dto.setSiteId(1001);
		dto.setBarCode("barCode");
		dto.setCreateTime(new Date());
		dto.setMarketPrice(1000F);
		dto.setShopId(1001);
		dto.setProductCode("productCode");
		dto.setProductName("productName");
		dto.setProductUrl("productUrl");
		dtoList.add(dto);
		productDao.batchInsert(dtoList);
	}

	@Test
	public void testBatchUpdate() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<String> codeList = new ArrayList<String>();
		codeList.add("productCode");
		List<ProductDto> dtoList = productDao.getProductDtos(codeList, null);
		dtoList = productDao.getProductDtos(codeList, null);
		for (ProductDto dto : dtoList) {
			dto.setProductAttr("update.attr.99999");
		}
		productDao.batchUpdate(dtoList);
	}

	@Test
	public void testBatchUpdateUnionUrls() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<String> codeList = new ArrayList<String>();
		codeList.add("productCode");
		List<ProductDto> dtoList = new ArrayList<ProductDto>();
		ProductDto dto = new ProductDto();
		dto.setProductAttr("updat.test");
		dto.setId(55221L);
		dto.setUnionUrl("unionUrl.test");
		dtoList.add(dto);
		productDao.batchUpdateUnionUrls(dtoList);
	}

	@Test
	public void testGetProductDtos() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<String> codeList = new ArrayList<String>();
		codeList.add("productCode");
		List<ProductDto> dtoList = productDao.getProductDtos(codeList, null);
		Assert.assertEquals(false, dtoList.isEmpty());
	}

	@Test
	public void testFillUnionUrls() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<String> codeList = new ArrayList<String>();
		codeList.add("productCode");
		List<ProductDto> dtoList = new ArrayList<ProductDto>();
		ProductDto dto = new ProductDto();
		dto.setProductAttr("updat.test");
		dto.setId(55221L);
		dto.setUnionUrl("unionUrl.test");
		dtoList.add(dto);
		productDao.batchUpdateUnionUrls(dtoList);
	}

	@Test
	public void testCopyCategoryNavFromStat() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/nav.txt"), "UTF-8");
		Map<String, String> codeNavMap = new HashMap<String, String>();
		for (String line : lineList) {
			String[] stringArr = line.split("\t");
			if (stringArr.length == 2) {
				codeNavMap.put(stringArr[0], stringArr[1]);
			} else {
				System.err.println(line);
			}
		}
		Integer siteId = 1002;
		BatchIterator<String> it = new BatchIterator<String>(new ArrayList<String>(codeNavMap.keySet()), 500);
		int total = codeNavMap.size();
		int count = 0;
		while (it.hasNext()) {
			List<String> codeList = it.next();
			List<ProductDto> productList = productDao.getProductDtos(codeList, siteId);
			for (ProductDto pDto : productList) {
				pDto.setCategoryNav(codeNavMap.get(pDto.getProductCode()));
			}
			productDao.batchUpdate(productList);
			count += codeList.size();
			System.err.println(count + "/" + total);
		}
	}
}
