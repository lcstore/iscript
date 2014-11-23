package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.SearchHisDto;

public interface SearchHisDao extends BaseDao<SearchHisDto> {
	SearchHisDto insertAndGetId(SearchHisDto dto);

	List<SearchHisDto> getSearchHisDtoByIds(@Param(value = "idList") List<Long> idList);

	SearchHisDto getSearchHisDtoBySolrQuery(@Param(value = "solrQuery") String solrQuery);

	List<SearchHisDto> getSearchHisDtoByStatus(@Param(value = "status") Integer status);

	void batchUpdateSearchHisDtoStatus(@Param(value = "idList") List<Long> idList, @Param(value = "status") int status);
}
