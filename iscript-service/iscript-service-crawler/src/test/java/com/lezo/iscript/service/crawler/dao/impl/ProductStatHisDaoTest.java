package com.lezo.iscript.service.crawler.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProductStatHisDao;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ProductStatHisDaoTest {

	@Test
	public void testBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductStatHisDao productStatHisDao = SpringBeanUtils.getBean(ProductStatHisDao.class);
        List<ProductStatDto> dtoList = new ArrayList<ProductStatDto>();
		for (int i = 0; i < 100; i++) {
			ProductStatDto monitorDto = new ProductStatDto();
			for (Field field : monitorDto.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Class<?> fieldType = field.getType();
				if (fieldType.equals(Integer.class)) {
					field.set(monitorDto, 100);
				} else if (fieldType.equals(String.class)) {
					field.set(monitorDto, "testString");
				} else if (fieldType.equals(Date.class)) {
					field.set(monitorDto, new Date());
				}
			}
			dtoList.add(monitorDto);
		}
		productStatHisDao.batchInsert(dtoList);
	}
}
