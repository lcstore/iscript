package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarService {
	void batchInsertSimilarDtos(List<SimilarDto> dtoList);

	void batchUpdateSimilarDtos(List<SimilarDto> dtoList);

	List<SimilarDto> getSimilarDtos(List<String> codeList, Integer siteId);

	List<SimilarDto> getSimilarDtoBySimilarCodes(List<Long> similarCodeList, List<Integer> siteList);

	List<Long> getSimilarCodeByCodeAsc(Long fromCode, Integer limit);

	List<SimilarDto> getSimilarDtoByCodeAndPrice(List<Long> sCodeList, List<String> pCodeList, Float fromPrice, Float toPrice, Integer offset, Integer limit);
	Integer getCountSimilarDtoByCodeAndPrice(List<Long> sCodeList, List<String> pCodeList, Float fromPrice, Float toPrice);
}
