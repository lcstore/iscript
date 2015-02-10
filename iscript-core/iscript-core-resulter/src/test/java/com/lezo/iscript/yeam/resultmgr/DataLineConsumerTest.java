package com.lezo.iscript.yeam.resultmgr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年2月6日
 */
public class DataLineConsumerTest {

	@Test
	public void testConsume() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		List<String> lineList = new ArrayList<String>();
		String line = FileUtils.readFileToString(new File("src/test/resources/data/ConfigProxySeedHandler.20150206.1423178014573"), "UTF-8");
		lineList.add(line);
		String type = "ConfigProxySeedHandler";
		for (String dataLine : lineList) {
			new DataLineConsumer(type, dataLine).run();
		}
	}

	@Test
	public void testProxyCheckConsume() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/data/ConfigProxyChecker.20150210.1423497600382"), "UTF-8");
		String type = "ConfigProxyChecker";
		for (String dataLine : lineList) {
			new DataLineConsumer(type, dataLine).run();
		}
		Thread.currentThread().join();
	}
}
