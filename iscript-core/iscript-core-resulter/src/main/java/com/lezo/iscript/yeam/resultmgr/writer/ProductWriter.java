package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class ProductWriter implements ObjectWriter<ProductDto> {
	private ProductService productService = SpringBeanUtils.getBean(ProductService.class);

	@Override
	public void write(List<ProductDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		synchronized (this) {
			productService.batchSaveProductDtos(dataList);
		}
	}

}
