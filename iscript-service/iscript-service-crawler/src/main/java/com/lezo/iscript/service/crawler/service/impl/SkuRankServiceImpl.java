package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dao.SkuRankDao;
import com.lezo.iscript.service.crawler.dto.SkuRankDto;
import com.lezo.iscript.service.crawler.service.SkuRankService;
import com.lezo.iscript.utils.BatchIterator;

public class SkuRankServiceImpl implements SkuRankService {
	@Autowired
	private SkuRankDao thisDao;

	@Override
	public int batchInsertDtos(List<SkuRankDto> dtoList) {
		BatchIterator<SkuRankDto> it = new BatchIterator<SkuRankDto>(dtoList);
		int affect = 0;
		while (it.hasNext()) {
			affect += thisDao.batchInsert(it.next());
		}
		return affect;
	}

	@Override
	public int batchUpdateDtos(List<SkuRankDto> dtoList) {
		BatchIterator<SkuRankDto> it = new BatchIterator<SkuRankDto>(dtoList);
		int affect = 0;
		while (it.hasNext()) {
			affect += thisDao.batchUpdate(it.next());
		}
		return affect;
	}

	@Override
	public int batchSaveDtos(List<SkuRankDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return 0;
		}
		List<SkuRankDto> insertList = new ArrayList<SkuRankDto>();
		List<SkuRankDto> updateList = new ArrayList<SkuRankDto>();
		doAssort(dtoList, insertList, updateList);
		int affect = batchInsertDtos(insertList);
		batchUpdateDtos(updateList);
		return affect;
	}

	private void doAssort(List<SkuRankDto> dtoList, List<SkuRankDto> insertList, List<SkuRankDto> updateList) {
		Map<Long, SkuRankDto> codeMap = new HashMap<Long, SkuRankDto>();
		for (SkuRankDto dto : dtoList) {
			codeMap.put(dto.getMatchCode(), dto);
		}
		List<SkuRankDto> hasList = getDtoByMatchCodes(new ArrayList<Long>(codeMap.keySet()));
		Set<Long> hasSet = new HashSet<Long>();
		for (SkuRankDto oldDto : hasList) {
			hasSet.add(oldDto.getMatchCode());
			SkuRankDto newDto = codeMap.get(oldDto.getMatchCode());
			newDto.setId(oldDto.getId());
			updateList.add(newDto);
		}
		for (SkuRankDto dto : dtoList) {
			if (hasSet.contains(dto.getMatchCode())) {
				continue;
			}
			insertList.add(dto);
		}

	}

	@Override
	public List<SkuRankDto> getDtoByIds(List<Long> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return Collections.emptyList();
		}
		return thisDao.getDtoByIds(idList);
	}

	@Override
	public List<SkuRankDto> getDtoByCategoryOrBarnd(String categroy, String brand) {
		if (StringUtils.isBlank(categroy) && StringUtils.isBlank(brand)) {
			return Collections.emptyList();
		}
		return thisDao.getDtoByCategoryOrBarnd(categroy, brand);
	}

	@Override
	public List<SkuRankDto> getDtoByMatchCodes(List<Long> matchCodeList) {
		if (CollectionUtils.isEmpty(matchCodeList)) {
			return Collections.emptyList();
		}
		return thisDao.getDtoByMatchCodes(matchCodeList);
	}

}
