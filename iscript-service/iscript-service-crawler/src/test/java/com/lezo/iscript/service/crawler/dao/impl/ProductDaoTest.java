package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProductDao;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ProductDaoTest {

	@Test
	public void testBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		List<ProductDto> dtoList = new ArrayList<ProductDto>();
		ProductDto dto = new ProductDto();
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
			dto.setProductAttr("update.attr.678");
		}
		productDao.batchUpdate(dtoList);
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
}
