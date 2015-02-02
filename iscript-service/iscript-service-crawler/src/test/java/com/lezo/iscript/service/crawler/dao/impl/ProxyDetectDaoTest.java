package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProxyDetectDao;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ProxyDetectDaoTest {

	@Test
	public void testBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProxyDetectDao proxyDetectDao = SpringBeanUtils.getBean(ProxyDetectDao.class);

		ProxyDetectDto dto = new ProxyDetectDto();
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		dto.setIpString("188.241.141.112");
		dto.setDomain("www.baidu.com");
		dto.setUrl("http://www.baidu.com/index.php?tn=19045005_9_pg");
		dto.setPort(100);
		dto.setDetector("d");
		dto.setCurCost(10L);
		dto.setMinCost(0L);
		dto.setMaxCost(1000L);
		dto.setRetryTimes(0);
		List<ProxyDetectDto> dtoList = new ArrayList<ProxyDetectDto>();
		dtoList.add(dto);
		proxyDetectDao.batchInsert(dtoList);
	}
}
