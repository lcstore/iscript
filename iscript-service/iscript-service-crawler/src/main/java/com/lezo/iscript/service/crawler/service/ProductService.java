package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProductDto;

public interface ProductService {
	void batchInsertProductDtos(List<ProductDto> dtoList);

	List<ProductDto> getProductDtos(List<String> codeList, Integer shopId, String siteCode);
}
