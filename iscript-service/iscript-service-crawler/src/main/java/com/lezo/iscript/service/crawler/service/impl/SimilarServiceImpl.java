package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.SimilarDao;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class SimilarServiceImpl implements SimilarService {
	@Autowired
	private SimilarDao similarDao;

	@Override
	public void batchInsertSimilarDtos(List<SimilarDto> dtoList) {
		BatchIterator<SimilarDto> it = new BatchIterator<SimilarDto>(dtoList);
		while (it.hasNext()) {
			similarDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateSimilarDtos(List<SimilarDto> dtoList) {
		BatchIterator<SimilarDto> it = new BatchIterator<SimilarDto>(dtoList);
		while (it.hasNext()) {
			similarDao.batchUpdate(it.next());
		}
	}

	@Override
	public List<SimilarDto> getSimilarDtos(List<String> codeList, Integer siteId) {
		List<SimilarDto> dtoList = new ArrayList<SimilarDto>();
		BatchIterator<String> it = new BatchIterator<String>(codeList);
		while (it.hasNext()) {
			List<SimilarDto> subList = similarDao.getSimilarDtos(it.next(), siteId);
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	@Override
	public List<SimilarDto> getSimilarDtoBySimilarCodes(List<Long> similarCodeList, List<Integer> siteList) {
		List<SimilarDto> dtoList = new ArrayList<SimilarDto>();
		BatchIterator<Long> it = new BatchIterator<Long>(similarCodeList);
		while (it.hasNext()) {
			List<SimilarDto> subList = similarDao.getSimilarDtoBySimilarCodes(it.next(), siteList);
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	public void setSimilarDao(SimilarDao similarDao) {
		this.similarDao = similarDao;
	}

	@Override
	public List<Long> getSimilarCodeByCodeAsc(Long fromCode, Integer limit) {
		if (limit == null || limit < 1) {
			return Collections.emptyList();
		}
		return this.similarDao.getSimilarCodeByCodeAsc(fromCode, limit);
	}

	@Override
	public List<SimilarDto> getSimilarDtoByCodeAndPrice(List<Long> sCodeList, List<String> pCodeList, Float fromPrice, Float toPrice, Integer offset, Integer limit) {
		if (CollectionUtils.isEmpty(sCodeList) && CollectionUtils.isEmpty(pCodeList)) {
			return Collections.emptyList();
		}
		return this.similarDao.getSimilarDtoByCodeAndPrice(sCodeList, pCodeList, fromPrice, toPrice, offset, limit);
	}

	@Override
	public Integer getCountSimilarDtoByCodeAndPrice(List<Long> sCodeList, List<String> pCodeList, Float fromPrice, Float toPrice) {
		if (CollectionUtils.isEmpty(sCodeList) && CollectionUtils.isEmpty(pCodeList)) {
			return 0;
		}
		return this.similarDao.getCountSimilarDtoByCodeAndPrice(sCodeList, pCodeList, fromPrice, toPrice);
	}

}
