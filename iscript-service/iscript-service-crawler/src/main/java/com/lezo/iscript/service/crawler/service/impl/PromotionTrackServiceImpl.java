package com.lezo.iscript.service.crawler.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.PromotionTrackDao;
import com.lezo.iscript.service.crawler.dto.PromotionTrackDto;
import com.lezo.iscript.service.crawler.service.PromotionTrackService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class PromotionTrackServiceImpl implements PromotionTrackService {
	@Autowired
	private PromotionTrackDao promotionTrackDao;

	@Override
	public void batchInsertDtos(List<PromotionTrackDto> dtoList) {
		BatchIterator<PromotionTrackDto> it = new BatchIterator<PromotionTrackDto>(dtoList);
		while (it.hasNext()) {
			promotionTrackDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateDtos(List<PromotionTrackDto> dtoList) {
		BatchIterator<PromotionTrackDto> it = new BatchIterator<PromotionTrackDto>(dtoList);
		while (it.hasNext()) {
			promotionTrackDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<PromotionTrackDto> dtoList) {

	}

	@Override
	public List<PromotionTrackDto> getPromotionTrackDtoByDate(Date sellDate, List<Integer> siteIdList) {
		if (sellDate == null) {
			return Collections.emptyList();
		}
		return promotionTrackDao.getPromotionTrackDtoByDate(sellDate, siteIdList);
	}

	@Override
	public void insertPromotionTrackDtoAndSetId(PromotionTrackDto dto) {
		if (dto == null) {
			return;
		}
		promotionTrackDao.insertPromotionTrackDtoAndSetId(dto);
	}

}
