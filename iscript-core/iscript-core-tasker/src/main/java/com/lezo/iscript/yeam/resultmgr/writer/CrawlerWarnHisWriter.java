package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.CrawlerWarnHisDto;
import com.lezo.iscript.service.crawler.service.CrawlerWarnHisService;

public class CrawlerWarnHisWriter implements ObjectWriter<CrawlerWarnHisDto>{
	private CrawlerWarnHisService crawlerWarnHisService;
	@Override
	public void write(List<CrawlerWarnHisDto> dataList) {
		crawlerWarnHisService.batchInsertDtos(dataList);
	}
}
