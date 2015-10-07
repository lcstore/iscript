package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.SimilarJobDto;

public interface SimilarJobService extends BaseService<SimilarJobDto> {
	List<SimilarJobDto> getDtoByIds(List<Long> idList);

    List<SimilarJobDto> getDtoByStatus(Long fromId, int statusReady, int limit);
}

