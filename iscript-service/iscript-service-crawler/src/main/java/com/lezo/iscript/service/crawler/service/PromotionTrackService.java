package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.PromotionTrackDto;

public interface PromotionTrackService extends BaseService<PromotionTrackDto> {
	void insertPromotionTrackDtoAndSetId(PromotionTrackDto dto);

	List<PromotionTrackDto> getPromotionTrackDtoByDate(Date sellDate, List<Integer> siteIdList);
}
