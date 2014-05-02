package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.TaskPriorityDao;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class TaskPriorityDaoImplTest {

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
	}
}
