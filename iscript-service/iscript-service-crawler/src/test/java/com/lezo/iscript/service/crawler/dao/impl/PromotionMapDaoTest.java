package com.lezo.iscript.service.crawler.dao.impl;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.PromotionMapDao;
import com.lezo.iscript.service.crawler.dto.PromotionMapDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class PromotionMapDaoTest {

	@Test
	public void testGetProductDtos() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		PromotionMapDao promotionMapDao = SpringBeanUtils.getBean(PromotionMapDao.class);
		Integer siteId = 1001;
		Integer promoteType = null;
		Integer promoteStatus = PromotionMapDto.PROMOTE_STATUS_START;
		Integer isDelete = PromotionMapDto.DELETE_FALSE;
		List<String> statList = promotionMapDao.getProductCodeSetBySiteIdAndType(siteId, promoteType, promoteStatus, isDelete);
		System.out.println(statList.size());
	}
}
