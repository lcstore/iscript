package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.ProductDao;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.intercept.IntercepterProxy;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ProxyTest {

	@Test
	public void test() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProductDao productDao = SpringBeanUtils.getBean(ProductDao.class);
		SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) SpringBeanUtils.getBean("sqlSessionFactory");

		Class<ProductDao> mapperInterface = ProductDao.class;
		IntercepterProxy<ProductDao> proxy = new IntercepterProxy<ProductDao>();
		proxy.setTargetObject(productDao);
		proxy.setSqlSessionFactory(sqlSessionFactory);
		productDao = proxy.getObject();

		List<ProductDto> dtoList = new ArrayList<ProductDto>();

		List<String> codeList = new ArrayList<String>();
		codeList.add("productCode");
		dtoList = productDao.getProductDtos(codeList, null);
		for (ProductDto dto : dtoList) {
			dto.setProductAttr("update.attr.22");
		}
		productDao.batchUpdate(dtoList);
	}
}
