package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.DataTransferDto;

public interface DataTransferService extends BaseService<DataTransferDto> {
	int batchInsertOrUpdateByKey(List<DataTransferDto> dtoList);

	List<DataTransferDto> getDtoByCodeList(List<String> codeList);

}
