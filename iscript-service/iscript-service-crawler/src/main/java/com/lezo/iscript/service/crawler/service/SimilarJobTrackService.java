package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.SimilarJobTrackDto;

public interface SimilarJobTrackService extends BaseService<SimilarJobTrackDto> {
	List<SimilarJobTrackDto> getDtoByIds(List<Long> idList);
}

