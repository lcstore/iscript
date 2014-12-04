package com.lezo.iscript.service.crawler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.CrawlerWarnHisDao;
import com.lezo.iscript.service.crawler.dto.CrawlerWarnHisDto;
import com.lezo.iscript.service.crawler.service.CrawlerWarnHisService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class CrawlerWarnHisServiceImpl implements CrawlerWarnHisService {
	@Autowired
	private CrawlerWarnHisDao crawlerWarnHisDao;

	@Override
	public void batchInsertDtos(List<CrawlerWarnHisDto> dtoList) {
		BatchIterator<CrawlerWarnHisDto> it = new BatchIterator<CrawlerWarnHisDto>(dtoList);
		while (it.hasNext()) {
			crawlerWarnHisDao.batchInsert(it.next());
		}

	}

	@Override
	public void batchUpdateDtos(List<CrawlerWarnHisDto> dtoList) {
		BatchIterator<CrawlerWarnHisDto> it = new BatchIterator<CrawlerWarnHisDto>(dtoList);
		while (it.hasNext()) {
			crawlerWarnHisDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<CrawlerWarnHisDto> dtoList) {

	}

}
