package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

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
@Log4j
public class ProductStatWriter implements ObjectWriter<ProductStatDto> {
	private ProductStatService productStatService = SpringBeanUtils.getBean(ProductStatService.class);

	@Override
	public void write(List<ProductStatDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		dataList = checkValue(dataList);
		synchronized (this) {
			productStatService.batchSaveProductStatDtos(dataList);
		}
	}

	private List<ProductStatDto> checkValue(List<ProductStatDto> dataList) {
		List<ProductStatDto> dtoList = new ArrayList<ProductStatDto>(dataList.size());
		for (ProductStatDto data : dataList) {
			if (data.getSiteId() == null) {
				log.warn("siteId is null,url:" + data.getProductUrl());
				continue;
			}
			if (data.getShopId() == null) {
				log.warn("shopId is null,url:" + data.getProductUrl());
				continue;
			}
			dtoList.add(data);
		}
		return dtoList;
	}
}
