package com.lezo.iscript.service.crawler.dao.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProductDao;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ProxyTest {

	@Test
	public void test() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);

		Class<ProductDao> mapperInterface = ProductDao.class;
		Object targetObject = productDao;
		InvocationHandler mapperProxy = new BatchUpdateProxy(targetObject);
		productDao = (ProductDao) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
				new Class[] { mapperInterface }, mapperProxy);
		List<ProductDto> dtoList = new ArrayList<ProductDto>();

		List<String> codeList = new ArrayList<String>();
		codeList.add("productCode");
		dtoList = productDao.getProductDtos(codeList, null, null);
		productDao.batchUpdate(dtoList);
	}
}
