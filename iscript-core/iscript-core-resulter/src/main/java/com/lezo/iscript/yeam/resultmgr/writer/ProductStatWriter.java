package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class ProductStatWriter implements ObjectWriter<ProductStatDto> {
	private ProductStatService productStatService = SpringBeanUtils.getBean(ProductStatService.class);

	@Override
	public void write(List<ProductStatDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		synchronized (this) {
			productStatService.batchSaveProductStatDtos(dataList);
		}
	}
}
