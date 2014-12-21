package com.lezo.iscript.service.crawler.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.BrandShopDao;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class BrandShopDaoTest {

	@Test
	public void testBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BrandShopDao brandShopDao = SpringBeanUtils.getBean(BrandShopDao.class);
		List<BrandShopDto> dtoList = new ArrayList<BrandShopDto>();
		for (int i = 0; i < 2; i++) {
			if (!dtoList.isEmpty()) {
				BrandShopDto dto = dtoList.get(0);
				BrandShopDto copyDto = (BrandShopDto) dto.clone();
				copyDto.setShopUrl(copyDto.getShopUrl() + i);
				dtoList.add(copyDto);
				continue;
			}
			BrandShopDto dto = new BrandShopDto();
			for (Field field : dto.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Class<?> fieldType = field.getType();
				if (fieldType.equals(Integer.class)) {
					field.set(dto, 100);
				} else if (fieldType.equals(String.class)) {
					field.set(dto, "testString");
				} else if (fieldType.equals(Date.class)) {
					field.set(dto, new Date());
				}
			}
			dtoList.add(dto);
		}
		brandShopDao.batchInsert(dtoList);
	}
}
