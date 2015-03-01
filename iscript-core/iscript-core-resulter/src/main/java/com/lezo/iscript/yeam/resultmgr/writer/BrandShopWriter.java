package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;
import com.lezo.iscript.service.crawler.service.BrandShopService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class BrandShopWriter implements ObjectWriter<BrandShopDto> {
	private BrandShopService brandShopService = SpringBeanUtils.getBean(BrandShopService.class);

	@Override
	public void write(List<BrandShopDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		synchronized (this) {
			brandShopService.batchSaveDtos(dataList);
		}
	}

}
