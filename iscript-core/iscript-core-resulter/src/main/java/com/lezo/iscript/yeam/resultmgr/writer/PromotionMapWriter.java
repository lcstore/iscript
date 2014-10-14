package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.PromotionMapDto;
import com.lezo.iscript.service.crawler.service.PromotionMapService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class PromotionMapWriter implements ObjectWriter<PromotionMapDto> {
	private PromotionMapService promotionMapService = SpringBeanUtils.getBean(PromotionMapService.class);

	@Override
	public void write(List<PromotionMapDto> dataList) {
		promotionMapService.batchSaveDtos(dataList);
	}

	@Override
	public void flush() {

	}

}
