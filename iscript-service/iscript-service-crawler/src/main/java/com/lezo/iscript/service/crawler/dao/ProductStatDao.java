package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public interface ProductStatDao {
	void batchInsert(List<ProductStatDto> dtoList);

	void batchUpdate(List<ProductStatDto> dtoList);

	List<ProductStatDto> getProductStatDtos(@Param(value = "codeList") List<String> codeList,
			@Param(value = "shopId") Integer shopId);

}