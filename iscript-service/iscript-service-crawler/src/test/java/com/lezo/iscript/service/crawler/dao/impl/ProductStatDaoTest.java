package com.lezo.iscript.service.crawler.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProductStatDao;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ProductStatDaoTest {

	@Test
	public void testGetProductDtos() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		Long fromId = 0L;
		Integer shopId = 1002;
		int limit = 4;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date updateTime = calendar.getTime();
		ProductStatDao productStatDao = SpringBeanUtils.getBean(ProductStatDao.class);
		List<ProductStatDto> statList = productStatDao.getProductStatDtosLowestPrice(fromId, shopId, updateTime, limit);
		System.out.println(statList.size());
	}
}
