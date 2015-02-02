package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;

public interface BarCodeItemService {

	void batchInsertBarCodeItemDtos(List<BarCodeItemDto> dtoList);

	void batchUpdateBarCodeItemDtos(List<BarCodeItemDto> dtoList);

	void batchSaveBarCodeItemDtos(List<BarCodeItemDto> dtoList);

	List<BarCodeItemDto> getBarCodeItemDtos(List<String> barCodeList);

	List<BarCodeItemDto> getBarCodeItemDtoFromId(Long fromId, int limit, String cateName);
}
