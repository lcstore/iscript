package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.ListRankDto;

public interface ListRankService extends BaseService<ListRankDto> {
	List<ListRankDto> getListRankDtos(String listUrl, List<String> codeList, Integer shopId);

}
