package com.lezo.iscript.service.crawler.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.PromotionTrackDao;
import com.lezo.iscript.service.crawler.dto.PromotionTrackDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class PromotionTrackDaoTest {

	@Test
	public void testBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		PromotionTrackDao promotionTrackDao = SpringBeanUtils.getBean(PromotionTrackDao.class);
		List<PromotionTrackDto> dtoList = new ArrayList<PromotionTrackDto>();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		Date fromTime = c.getTime();
		PromotionTrackDto dto = new PromotionTrackDto();
		dto.setSiteId(0);
		dto.setProductCode("40271259575");
		dto.setProductName("苏醒的乐园 2014冬装新款新品修身 加厚羽绒服女短款 外套YRF258");
		dto.setProductUrl("http://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.44.tEE8nC&id=40271259575");
		dto.setPromotionDetail("1212感恩价268.9元　加入购物车！！ ");
		dto.setFromPrice(699F);
		dto.setTargetPrice(26839.9F);
		// dto.setToPrice(toPrice);
		dto.setFromTime(fromTime);
		c.add(Calendar.DAY_OF_MONTH, 1);
		dto.setToTime(c.getTime());
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());

		dtoList.add(dto);
		promotionTrackDao.batchInsert(dtoList);
	}
}
