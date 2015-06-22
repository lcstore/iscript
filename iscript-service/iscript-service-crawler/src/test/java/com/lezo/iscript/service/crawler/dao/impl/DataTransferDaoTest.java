package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.DataTransferDao;
import com.lezo.iscript.service.crawler.dto.DataTransferDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class DataTransferDaoTest {

	@Test
	public void testBatchInsertOrUpdateByIndexKey() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		DataTransferDao dataTransferDao = SpringBeanUtils.getBean(DataTransferDao.class);
		List<DataTransferDto> dtoList = new ArrayList<DataTransferDto>();
		for (int i = 0; i < 10; i++) {
			DataTransferDto dto = new DataTransferDto();
			dto.setDataBucket("bucket_" + i);
			dto.setDataDomain("domain");
			dto.setDataPath("path_" + i);
			dto.setParams("params_" + i);
			dto.setCreateTime(new Date());
			dto.setUpdateTime(new Date());
			dto.setDataCount(100);
			dtoList.add(dto);
		}
		for (DataTransferDto dto : dtoList) {
			StringBuilder sb = new StringBuilder();
			sb.append(dto.getDataBucket());
			sb.append(dto.getDataPath());
			sb.append(dto.getDataDomain());
			dto.setDataCode(sb.toString());
		}
		int count = dataTransferDao.batchInsertOrUpdateByKey(dtoList);
		System.err.println(count);
	}
}
