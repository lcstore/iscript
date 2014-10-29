package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public interface ProductStatDao {
	void batchInsert(List<ProductStatDto> dtoList);

	void batchUpdate(List<ProductStatDto> dtoList);

	List<ProductStatDto> getProductStatDtos(@Param(value = "codeList") List<String> codeList, @Param(value = "siteId") Integer siteId, @Param(value = "minStock") Integer minStock);

	List<ProductStatDto> getProductStatDtosByCommentDesc(@Param(value = "siteId") Integer siteId, @Param(value = "limit") int limit);

	List<ProductStatDto> getProductStatDtosByPriceAsc(@Param(value = "siteId") Integer siteId, @Param(value = "limit") int limit);

	List<ProductStatDto> getProductStatDtosBySoldDesc(@Param(value = "siteId") Integer siteId, @Param(value = "limit") int limit);

	List<ProductStatDto> getProductStatDtosLowestPrice(@Param(value = "fromId") Long fromId, @Param(value = "siteId") Integer siteId, @Param(value = "updateTime") Date updateTime, @Param(value = "limit") int limit);

}
