package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.MessageDao;
import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class MessageDaoTest {

	@Test
	public void testGetEarlyMessageIdByNameList() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		MessageDao messageDao = SpringBeanUtils.getBean(MessageDao.class);

		List<String> nameList = new ArrayList<String>();
		nameList.add("ConfigProxyDetector");
		nameList.add("ConfigProxySeedHandler");

		List<MessageDto> mapList = messageDao.getEarlyMessageByNameList(nameList, 0);
		System.err.println(mapList.size());

		Set<Long> idSet = new HashSet<Long>();
		for (MessageDto map : mapList) {
			idSet.add(map.getId());
		}
		List<Long> idList = new ArrayList<Long>(idSet);
		List<MessageDto> dtoList = messageDao.getMessageDtoByIdList(idList);
		System.err.println(dtoList.size());

	}
}
