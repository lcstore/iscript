package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.BarCodeItemDao;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

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
		dto.setShopId(1001);
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
		List<BarCodeItemDto> dtoList = barCodeItemDao.getBarCodeItemDtos(null, barCodeList);
		Assert.assertEquals(false, dtoList.isEmpty());
	}
}
