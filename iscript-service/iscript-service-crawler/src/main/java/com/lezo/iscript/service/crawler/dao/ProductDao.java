package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.ProductDto;

public interface ProductDao {
	void batchInsert(List<ProductDto> dtoList);

	void batchUpdate(List<ProductDto> dtoList);

	List<ProductDto> getProductDtos(@Param(value = "codeList") List<String> codeList,
			@Param(value = "shopId") Integer shopId);

	List<ProductDto> getProductDtosFromId(@Param(value = "fromId") Long fromId, @Param(value = "limit") int limit,
			@Param(value = "shopId") Integer shopId);

}
