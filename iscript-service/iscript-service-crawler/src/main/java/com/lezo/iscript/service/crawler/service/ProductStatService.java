package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public interface ProductStatService {
	void batchInsertProductStatDtos(List<ProductStatDto> dtoList);

	void batchUpdateProductStatDtos(List<ProductStatDto> dtoList);

	void batchSaveProductStatDtos(List<ProductStatDto> dtoList);

	List<ProductStatDto> getProductStatDtos(List<String> codeList, Integer siteId, Integer minStock);

	List<ProductStatDto> getProductStatDtosByCommentDesc(Integer siteId, int limit);

	List<ProductStatDto> getProductStatDtosByPriceAsc(Integer siteId, int limit);

	List<ProductStatDto> getProductStatDtosBySoldDesc(Integer siteId, int limit);

	List<ProductStatDto> getProductStatDtosLowestPrice(Long fromId, Integer siteId, Date updateTime, int limit);
}
