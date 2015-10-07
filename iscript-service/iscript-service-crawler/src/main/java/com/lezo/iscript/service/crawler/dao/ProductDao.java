package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.Batch;
import com.lezo.iscript.service.crawler.dto.ProductDto;

public interface ProductDao {
	void batchInsert(List<ProductDto> dtoList);

	void batchUpdate(@Batch List<ProductDto> dtoList);

	List<ProductDto> getProductDtos(@Param(value = "codeList") List<String> codeList,
			@Param(value = "siteId") Integer siteId);

	List<ProductDto> getProductDtosFromId(@Param(value = "fromId") Long fromId, @Param(value = "limit") int limit,
			@Param(value = "siteId") Integer siteId);

	void batchUpdateUnionUrls(List<ProductDto> dtoList);

	List<ProductDto> getProductDtosByDateCateSiteId(@Param("fromDate") Date fromCreateDate,
			@Param("toDate") Date toCreateDate, @Param("tokenCategory") String tokenCategory,
			@Param("siteId") Integer siteId, @Param("fromId") Long fromId, @Param("limit") int limit);

    void batchUpdateBarCodeBySkuCode(@Batch List<ProductDto> dtoList);

}
