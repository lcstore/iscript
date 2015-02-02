package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarDao {
	void batchInsert(List<SimilarDto> dtoList);

	void batchUpdate(List<SimilarDto> dtoList);

	List<SimilarDto> getSimilarDtos(@Param(value = "codeList") List<String> codeList, @Param(value = "siteId") Integer siteId);

	List<SimilarDto> getSimilarDtoBySimilarCodes(@Param(value = "similarCodeList") List<Long> similarCodeList, @Param(value = "siteIdList") List<Integer> siteIdList);

	List<Long> getSimilarCodeByCodeAsc(@Param("fromCode") Long fromCode, @Param("limit") Integer limit);

	List<SimilarDto> getSimilarDtoByCodeAndPrice(@Param("sCodeList") List<Long> sCodeList, @Param("pCodeList") List<String> pCodeList, @Param("fromPrice") Float fromPrice,
			@Param("toPrice") Float toPrice, @Param("offset") Integer offset, @Param("limit") Integer limit);

	Integer getCountSimilarDtoByCodeAndPrice(@Param("sCodeList") List<Long> sCodeList, @Param("pCodeList") List<String> pCodeList, @Param("fromPrice") Float fromPrice, @Param("toPrice") Float toPrice);
}
