package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.ProductDto;

public interface ProductStatDao {
	void batchInsert(List<ProductDto> dtoList);

	List<ProductDto> getProductDtos(@Param(value = "codeList") List<String> codeList,
			@Param(value = "shopId") Integer shopId, @Param(value = "siteCode") String siteCode);

}
