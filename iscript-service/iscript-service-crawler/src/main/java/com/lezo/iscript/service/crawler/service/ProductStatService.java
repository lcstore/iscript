package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public interface ProductStatService {
	void batchInsertProductStatDtos(List<ProductStatDto> dtoList);

	void batchUpdateProductStatDtos(List<ProductStatDto> dtoList);

	void batchSaveProductStatDtos(List<ProductStatDto> dtoList);

	List<ProductStatDto> getProductStatDtos(List<String> codeList, Integer shopId);

	List<ProductStatDto> getProductStatDtosByCommentDesc(Integer shopId, int limit);

	List<ProductStatDto> getProductStatDtosByPriceAsc(Integer shopId, int limit);

	List<ProductStatDto> getProductStatDtosBySoldDesc(Integer shopId, int limit);

	List<ProductStatDto> getProductStatDtosLowestPrice(Long fromId, Integer shopId, Date updateTime, int limit);
}
