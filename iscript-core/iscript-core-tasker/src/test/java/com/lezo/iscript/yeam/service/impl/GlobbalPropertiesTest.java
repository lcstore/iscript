package com.lezo.iscript.yeam.service.impl;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.property.GlobalProperties;

public class GlobbalPropertiesTest {
	// public static final int MIN_TASK_SIZE =
	// GlobalProperties.getInstance().getMinTaskSize();

	@Test
	public void testConfig() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		GlobalProperties globalProperties = SpringBeanUtils.getBean(GlobalProperties.class);
		System.err.println("MIN_TASK_SIZE:" + globalProperties.getMinTaskSize());
	}
}
