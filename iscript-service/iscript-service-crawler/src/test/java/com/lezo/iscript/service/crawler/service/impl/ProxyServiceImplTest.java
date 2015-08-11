package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.common.UnifyValueUtils;
import com.lezo.iscript.service.crawler.dao.ProxyAddrDao;
import com.lezo.iscript.service.crawler.dao.ProxyDetectDao;
import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.URLUtils;

public class ProxyServiceImplTest {
	// private static final String DEFAULT_DETECT_URL = "http://www.baidu.com/";
	private static final String DEFAULT_DETECT_URL = "http://www.yhd.com/";

	@Test
	public void testBatchInsertIfAbsent() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ProxyAddrDao proxyAddrDao = SpringBeanUtils.getBean(ProxyAddrDao.class);
		ProxyDetectDao proxyDetectDao = SpringBeanUtils.getBean(ProxyDetectDao.class);
		ProxyDetectServiceImpl proxyDetectService = new ProxyDetectServiceImpl();
		proxyDetectService.setProxyDetectDao(proxyDetectDao);

		Long fromId = 4421519L;
		Integer limit = 1000;
		int sum = 0;
		long startMills = System.currentTimeMillis();
		while (true) {
			long startQueryMills = System.currentTimeMillis();
			List<ProxyAddrDto> batchAddrList = proxyAddrDao.getProxyAddrDtosByFromId(fromId, limit);
			List<ProxyDetectDto> dtoList = convertTo(batchAddrList);
			proxyDetectService.batchInsertIfAbsent(dtoList);
			sum += batchAddrList.size();
			long insertMills = System.currentTimeMillis() - startQueryMills;
			System.err.println(batchAddrList.size() + "/" + sum + ",cost:" + insertMills);
			for (ProxyAddrDto addrDto : batchAddrList) {
				if (fromId < addrDto.getId()) {
					fromId = addrDto.getId();
				}
			}
			if (batchAddrList.size() < limit) {
				break;
			}
		}
		long cost = System.currentTimeMillis() - startMills;
		System.out.println("done,cost:" + cost + ",maxId:" + fromId);
	}

	private List<ProxyDetectDto> convertTo(List<ProxyAddrDto> batchAddrList) throws Exception {
		if (batchAddrList.isEmpty()) {
			return Collections.emptyList();
		}
		List<ProxyDetectDto> detectList = new ArrayList<ProxyDetectDto>(batchAddrList.size());
		Date currentDate = new Date();
		String domain = URLUtils.getRootHost(DEFAULT_DETECT_URL);
		for (ProxyAddrDto dto : batchAddrList) {
			ProxyDetectDto detectDto = new ProxyDetectDto();
			detectDto.setAddrCode(dto.getAddrCode());
			detectDto.setIp(dto.getIp());
			detectDto.setPort(dto.getPort());
			detectDto.setCreateTime(currentDate);
			detectDto.setUpdateTime(currentDate);
			detectDto.setType(dto.getType());
			detectDto.setUrl(DEFAULT_DETECT_URL);
			detectDto.setDomain(domain);
			detectDto = UnifyValueUtils.unifyObject(detectDto);
			detectList.add(detectDto);
		}
		return detectList;
	}

}
