package com.lezo.iscript.service.crawler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.LuceneIndexDao;
import com.lezo.iscript.service.crawler.dto.LuceneIndexDto;
import com.lezo.iscript.service.crawler.service.LuceneIndexService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class LuceneIndexServiceImpl implements LuceneIndexService {
	@Autowired
	private LuceneIndexDao luceneIndexDao;

	@Override
	public void batchInsertDtos(List<LuceneIndexDto> dtoList) {
		BatchIterator<LuceneIndexDto> it = new BatchIterator<LuceneIndexDto>(dtoList);
		while (it.hasNext()) {
			luceneIndexDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateDtos(List<LuceneIndexDto> dtoList) {
		BatchIterator<LuceneIndexDto> it = new BatchIterator<LuceneIndexDto>(dtoList);
		while (it.hasNext()) {
			luceneIndexDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<LuceneIndexDto> dtoList) {
		batchInsertDtos(dtoList);
	}

	@Override
	public LuceneIndexDto getLatestLuceneIndexDto(Integer status) {
		return luceneIndexDao.getLatestLuceneIndexDto(status);
	}

	public void setLuceneIndexDao(LuceneIndexDao luceneIndexDao) {
		this.luceneIndexDao = luceneIndexDao;
	}

}
