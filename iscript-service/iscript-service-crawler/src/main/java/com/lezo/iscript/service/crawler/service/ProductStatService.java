package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public interface ProductStatService {
	void batchInsertProductStatDtos(List<ProductStatDto> dtoList);

	void batchUpdateProductStatDtos(List<ProductStatDto> dtoList);

	void batchSaveProductStatDtos(List<ProductStatDto> dtoList);

	List<ProductStatDto> getProductStatDtos(List<String> codeList, Integer shopId);
}
