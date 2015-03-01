package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.CrawlerWarnHisDto;

public interface CrawlerWarnHisDao{
	void batchInsert(List<CrawlerWarnHisDto> dtoList);
}
