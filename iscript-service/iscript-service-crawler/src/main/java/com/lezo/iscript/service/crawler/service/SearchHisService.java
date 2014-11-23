package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.SearchHisDto;

public interface SearchHisService extends BaseService<SearchHisDto> {
	Long saveSearchHisDtoAndGetId(SearchHisDto dto);

	List<SearchHisDto> getSearchHisDtoByIds(List<Long> idList);

	SearchHisDto getSearchHisDtoBySolrQuery(String solrQuery);
}
