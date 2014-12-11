package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.CrawlerWarnHisDto;
import com.lezo.iscript.service.crawler.service.CrawlerWarnHisService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class CrawlerWarnHisWriter implements ObjectWriter<CrawlerWarnHisDto> {
	private CrawlerWarnHisService crawlerWarnHisService = SpringBeanUtils.getBean(CrawlerWarnHisService.class);

	@Override
	public void write(List<CrawlerWarnHisDto> dataList) {
		crawlerWarnHisService.batchInsertDtos(dataList);
	}
}
