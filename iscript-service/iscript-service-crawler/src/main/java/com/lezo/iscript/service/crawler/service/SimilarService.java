package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarService {
	void batchInsertSimilarDtos(List<SimilarDto> dtoList);

	void batchUpdateSimilarDtos(List<SimilarDto> dtoList);

	List<SimilarDto> getSimilarDtos(List<String> codeList, Integer shopId);

	List<SimilarDto> getSimilarDtoBySimilarCodes(List<Long> similarCodeList, List<Integer> shopIds);
}
