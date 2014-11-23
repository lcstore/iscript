package com.lezo.iscript.service.crawler.service;

import java.util.Date;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.LuceneIndexDto;

public interface LuceneIndexService extends BaseService<LuceneIndexDto> {
	LuceneIndexDto getLatestLuceneIndexDto(Integer status);

	LuceneIndexDto getLuceneIndexDtoByDay(Date indexDay);
}
