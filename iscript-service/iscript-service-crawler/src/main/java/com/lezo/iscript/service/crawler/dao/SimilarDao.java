package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarDao {
	void batchInsert(List<SimilarDto> dtoList);

	void batchUpdate(List<SimilarDto> dtoList);

	List<SimilarDto> getSimilarDtos(@Param(value = "codeList") List<String> codeList,
			@Param(value = "siteId") Integer siteId);

	List<SimilarDto> getSimilarDtoBySimilarCodes(@Param(value = "similarCodeList") List<Long> similarCodeList,
			@Param(value = "siteIdList") List<Integer> siteIdList);

}
