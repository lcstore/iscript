package com.lezo.iscript.service.crawler;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DaoBaseTest {

	private ClassPathXmlApplicationContext cx;

	public DaoBaseTest() {
		this(new String[] { "classpath:spring-config-ds.xml" });
	}

	public DaoBaseTest(String[] configs) {
		this.cx = new ClassPathXmlApplicationContext(configs);
	}

	public <T> T getBean(Class<T> newClass) throws BeansException {
		return this.cx.getBean(newClass);
	}
}
