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
	public List<SimilarDto> getSimilarDtoByProductCodes(Integer siteId, List<String> codeList) {
		if (siteId == null || siteId <= 0 || CollectionUtils.isEmpty(codeList)) {
			return Collections.emptyList();
		}
		List<SimilarDto> dtoList = new ArrayList<SimilarDto>();
		BatchIterator<String> it = new BatchIterator<String>(codeList);
		while (it.hasNext()) {
			List<SimilarDto> subList = similarDao.getSimilarDtoByProductCodes(siteId, it.next());
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	public void setSimilarDao(SimilarDao similarDao) {
		this.similarDao = similarDao;
	}

}
