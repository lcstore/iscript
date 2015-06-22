package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.common.Batch;
import com.lezo.iscript.service.crawler.dto.DataTransferDto;

public interface DataTransferDao extends BaseDao<DataTransferDto> {
	int batchUpdateByCode(@Batch @Param("dtoList") List<DataTransferDto> dtoList);

	/**
	 * Only support in mysql
	 * 
	 * @param dtoList
	 * @return
	 */
	int batchInsertOrUpdateByKey(List<DataTransferDto> dtoList);

	List<DataTransferDto> getDtoByCodeList(@Param("codeList") List<String> codeList);

}
