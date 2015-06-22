package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.SearchHisDao;
import com.lezo.iscript.service.crawler.dto.SearchHisDto;
import com.lezo.iscript.service.crawler.service.SearchHisService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class SearchHisServiceImpl implements SearchHisService {
	@Autowired
	private SearchHisDao searchHisDao;

	@Override
	public int batchInsertDtos(List<SearchHisDto> dtoList) {
		int affect = 0;
		BatchIterator<SearchHisDto> it = new BatchIterator<SearchHisDto>(dtoList);
		while (it.hasNext()) {
			affect += searchHisDao.batchInsert(it.next());
		}
		return affect;
	}

	@Override
	public int batchUpdateDtos(List<SearchHisDto> dtoList) {
		int affect = 0;
		BatchIterator<SearchHisDto> it = new BatchIterator<SearchHisDto>(dtoList);
		while (it.hasNext()) {
			affect += searchHisDao.batchUpdate(it.next());
		}
		return affect;
	}

	@Override
	public SearchHisDto saveSearchHisDtoAndGetId(SearchHisDto dto) {
		if (StringUtils.isEmpty(dto.getQuerySolr())) {
			throw new IllegalArgumentException("querySolr must not be null..");
		}
		SearchHisDto hasDto = getSearchHisDtoBySolrQuery(dto.getQuerySolr());
		if (hasDto == null) {
			this.searchHisDao.insertAndGetId(dto);
			return dto;
		} else if (hasDto.getStatus() < 0 || isExpired(dto)) {
			hasDto.setStatus(dto.getStatus());
			List<SearchHisDto> dtoList = new ArrayList<SearchHisDto>(1);
			dtoList.add(hasDto);
			batchUpdateDtos(dtoList);
		}
		return hasDto;
	}

	private boolean isExpired(SearchHisDto hasDto) {
		return System.currentTimeMillis() - hasDto.getUpdateTime().getTime() >= SearchHisDto.EXPIRED_TIME;
	}

	@Override
	public List<SearchHisDto> getSearchHisDtoByIds(List<Long> idList) {
		List<SearchHisDto> resultList = new ArrayList<SearchHisDto>();
		BatchIterator<Long> it = new BatchIterator<Long>(idList, 500);
		while (it.hasNext()) {
			List<SearchHisDto> subList = this.searchHisDao.getSearchHisDtoByIds(it.next());
			if (!subList.isEmpty()) {
				resultList.addAll(subList);
			}
		}
		return resultList;
	}

	@Override
	public SearchHisDto getSearchHisDtoBySolrQuery(String solrQuery) {
		if (StringUtils.isEmpty(solrQuery)) {
			return null;
		}
		return this.searchHisDao.getSearchHisDtoBySolrQuery(solrQuery);
	}

	public void setSearchHisDao(SearchHisDao searchHisDao) {
		this.searchHisDao = searchHisDao;
	}

	@Override
	@Deprecated
	public int batchSaveDtos(List<SearchHisDto> dtoList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SearchHisDto> getSearchHisDtoByStatus(Integer status) {
		return this.searchHisDao.getSearchHisDtoByStatus(status);
	}

	@Override
	public void batchUpdateSearchHisDtoStatus(List<Long> idList, int status) {
		if (CollectionUtils.isEmpty(idList)) {
			return;
		}
		this.searchHisDao.batchUpdateSearchHisDtoStatus(idList, status);
	}

}
