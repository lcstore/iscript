package com.lezo.iscript.service.crawler.service;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.LuceneIndexDto;

public interface LuceneIndexService extends BaseService<LuceneIndexDto> {
	LuceneIndexDto getLatestLuceneIndexDto(Integer status);
}
