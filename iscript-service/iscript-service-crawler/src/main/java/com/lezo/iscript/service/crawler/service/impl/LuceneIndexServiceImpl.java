package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	public int batchInsertDtos(List<LuceneIndexDto> dtoList) {
		int affect = 0;
		BatchIterator<LuceneIndexDto> it = new BatchIterator<LuceneIndexDto>(dtoList);
		while (it.hasNext()) {
			affect += luceneIndexDao.batchInsert(it.next());
		}
		return affect;
	}

	@Override
	public int batchUpdateDtos(List<LuceneIndexDto> dtoList) {
		int affect = 0;
		BatchIterator<LuceneIndexDto> it = new BatchIterator<LuceneIndexDto>(dtoList);
		while (it.hasNext()) {
			affect += luceneIndexDao.batchUpdate(it.next());
		}
		return affect;
	}

	@Override
	public int batchSaveDtos(List<LuceneIndexDto> dtoList) {
		int affect = 0;
		List<LuceneIndexDto> insertList = new ArrayList<LuceneIndexDto>();
		List<LuceneIndexDto> updateList = new ArrayList<LuceneIndexDto>();
		for (LuceneIndexDto dto : dtoList) {
			LuceneIndexDto hasDto = getLuceneIndexDtoByDay(dto.getDataDay());
			if (hasDto != null) {
				dto.setId(hasDto.getId());
				updateList.add(dto);
			} else {
				insertList.add(dto);
			}
		}
		affect += batchInsertDtos(insertList);
		affect += batchUpdateDtos(dtoList);
		return affect;
	}

	@Override
	public LuceneIndexDto getLatestLuceneIndexDto(Integer status) {
		return luceneIndexDao.getLatestLuceneIndexDto(status);
	}

	public void setLuceneIndexDao(LuceneIndexDao luceneIndexDao) {
		this.luceneIndexDao = luceneIndexDao;
	}

	@Override
	public LuceneIndexDto getLuceneIndexDtoByDay(Date indexDay) {
		Calendar c = Calendar.getInstance();
		c.setTime(indexDay);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return this.luceneIndexDao.getLuceneIndexDtoByDay(c.getTime());
	}

}
