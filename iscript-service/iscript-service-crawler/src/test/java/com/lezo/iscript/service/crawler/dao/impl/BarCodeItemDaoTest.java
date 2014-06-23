package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.BarCodeItemDao;
import com.lezo.iscript.service.crawler.dao.TaskPriorityDao;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class BarCodeItemDaoTest {

	@Test
	public void test() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		TaskPriorityDao taskPriorityDao = SpringBeanUtils.getBean(TaskPriorityDaoImpl.class);
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
		TaskPriorityDto e = new TaskPriorityDto();
		e.setBatchId("bid");
		e.setLevel(0);
		e.setSource("test");
		e.setStatus(0);
		e.setType("test-type");
		dtoList.add(e);
		taskPriorityDao.batchInsert(dtoList);
		MapperFactoryBean<BarCodeItemDao> bean = new MapperFactoryBean<BarCodeItemDao>();
		bean.setMapperInterface(BarCodeItemDao.class);
		bean.setSqlSessionFactory(SpringBeanUtils.getBean(SqlSessionFactory.class));
	}
}
