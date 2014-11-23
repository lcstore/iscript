package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.SearchHisDto;

public interface SearchHisService extends BaseService<SearchHisDto> {
	SearchHisDto saveSearchHisDtoAndGetId(SearchHisDto dto);

	List<SearchHisDto> getSearchHisDtoByIds(List<Long> idList);

	SearchHisDto getSearchHisDtoBySolrQuery(String solrQuery);

	List<SearchHisDto> getSearchHisDtoByStatus(Integer status);

	void batchUpdateSearchHisDtoStatus(List<Long> idList, int status);
}
