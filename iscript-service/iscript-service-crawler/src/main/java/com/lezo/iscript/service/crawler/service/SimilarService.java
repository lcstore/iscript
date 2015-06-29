package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarService {
	void batchInsertSimilarDtos(List<SimilarDto> dtoList);

	void batchUpdateSimilarDtos(List<SimilarDto> dtoList);

	void batchSaveSimilarDtos(List<SimilarDto> dtoList);

	List<SimilarDto> getSimilarDtoByProductCodes(Integer siteId, List<String> codeList);

	List<SimilarDto> getSimilarDtoBySimilarCodes(List<Long> codeList);

}
