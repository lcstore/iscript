package com.lezo.iscript.service.crawler.service.impl;

import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.service.SynonymBrandService;

public class SynonymBrandServiceImplTest {

	@Test
	public void testGetSynonyms() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		SynonymBrandService synonymBrandService = new SynonymBrandServiceImpl();
		String brandName = "苹果";
		Set<String> brandSet = synonymBrandService.getSynonyms(brandName);
		System.err.println(brandName + ":" + ArrayUtils.toString(brandSet.toArray()));
		brandName = "德运";
		brandSet = synonymBrandService.getSynonyms(brandName);
		System.err.println(brandName + ":" + ArrayUtils.toString(brandSet.toArray()));
	}

}
