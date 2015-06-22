package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.Batch;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;

public interface BarCodeItemDao {
	void batchInsert(List<BarCodeItemDto> dtoList);

	void batchUpdate(@Batch List<BarCodeItemDto> dtoList);

	List<BarCodeItemDto> getBarCodeItemDtos(List<String> barCodeList);

	List<BarCodeItemDto> getBarCodeItemDtoFromId(@Param(value = "fromId") Long fromId,
			@Param(value = "limit") int limit, @Param(value = "cateName") String cateName);

	Integer deleteFromId(List<Long> idList);
}
