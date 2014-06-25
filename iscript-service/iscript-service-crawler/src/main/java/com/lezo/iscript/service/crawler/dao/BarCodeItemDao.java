package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;

public interface BarCodeItemDao {
	void batchInsert(List<BarCodeItemDto> dtoList);

	List<BarCodeItemDto> getBarCodeItemDtos(@Param(value = "barCodeList") List<String> barCodeList,
			@Param(value = "shopCode") String shopCode);

}
