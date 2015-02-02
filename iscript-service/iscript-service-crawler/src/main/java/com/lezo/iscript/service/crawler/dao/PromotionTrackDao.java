package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.PromotionTrackDto;

public interface PromotionTrackDao extends BaseDao<PromotionTrackDto> {
	List<PromotionTrackDto> getPromotionTrackDtoByDate(@Param("sellDate") Date sellDate, @Param("siteList") List<Integer> siteList);

	void insertPromotionTrackDtoAndSetId(PromotionTrackDto dto);
}
