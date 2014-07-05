package com.lezo.iscript.yeam.crawler;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.spring.context.SpringBeanUtils;

public class PullJdListTimerTest {

	@Test
	public void testPullJd(){
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		PullJdListTimer timer = SpringBeanUtils.getBean(PullJdListTimer.class);
		timer.run();
	}
}
