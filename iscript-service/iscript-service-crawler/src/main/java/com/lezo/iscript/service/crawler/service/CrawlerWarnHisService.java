package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.CrawlerWarnHisDto;

public interface CrawlerWarnHisService {
	void batchInsertDtos(List<CrawlerWarnHisDto> dtoList);
}
