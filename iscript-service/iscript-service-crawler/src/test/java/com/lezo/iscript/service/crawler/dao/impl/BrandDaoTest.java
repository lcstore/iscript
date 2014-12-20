package com.lezo.iscript.service.crawler.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.BrandDao;
import com.lezo.iscript.service.crawler.dto.BrandDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class BrandDaoTest {

	@Test
	public void testBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BrandDao brandDao = SpringBeanUtils.getBean(BrandDao.class);
		List<BrandDto> dtoList = new ArrayList<BrandDto>();
		for (int i = 0; i < 50; i++) {
			if (!dtoList.isEmpty()) {
				BrandDto dto = dtoList.get(0);
				BrandDto copyDto = (BrandDto) dto.clone();
				copyDto.setBrandUrl(copyDto.getBrandUrl() + i);
				dtoList.add(copyDto);
				continue;
			}
			BrandDto monitorDto = new BrandDto();
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
		brandDao.batchInsert(dtoList);
	}
}
