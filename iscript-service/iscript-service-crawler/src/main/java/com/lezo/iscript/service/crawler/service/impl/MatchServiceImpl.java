package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.MatchDao;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class MatchServiceImpl implements MatchService {
	@Autowired
	private MatchDao matchDao;

	@Override
	public int batchInsertDtos(List<MatchDto> dtoList) {
		int affect = 0;
		BatchIterator<MatchDto> it = new BatchIterator<MatchDto>(dtoList);
		while (it.hasNext()) {
			affect += matchDao.batchInsert(it.next());
		}
		return affect;
	}

	@Override
	public int batchUpdateDtos(List<MatchDto> dtoList) {
		int affect = 0;
		BatchIterator<MatchDto> it = new BatchIterator<MatchDto>(dtoList);
		while (it.hasNext()) {
			affect += matchDao.batchUpdate(it.next());
		}
		return affect;
	}

	@Override
	public int batchSaveDtos(List<MatchDto> dtoList) {
		List<MatchDto> insertDtos = new ArrayList<MatchDto>();
		List<MatchDto> updateDtos = new ArrayList<MatchDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchUpdateDtos(updateDtos);
		return batchInsertDtos(insertDtos);
	}

	private void doAssort(List<MatchDto> dtoList, List<MatchDto> insertDtos, List<MatchDto> updateDtos) {
		Map<Long, List<MatchDto>> mCode2DtoMap = new HashMap<Long, List<MatchDto>>();
		for (MatchDto dto : dtoList) {
			List<MatchDto> matchDtos = mCode2DtoMap.get(dto.getMatchCode());
			if (matchDtos == null) {
				matchDtos = new ArrayList<MatchDto>();
				mCode2DtoMap.put(dto.getMatchCode(), matchDtos);
			}
			matchDtos.add(dto);
		}
		for (Entry<Long, List<MatchDto>> entry : mCode2DtoMap.entrySet()) {
			Map<String, MatchDto> keyMap = new HashMap<String, MatchDto>();
			Map<Integer, Set<String>> siteCodeMap = new HashMap<Integer, Set<String>>();
			for (MatchDto dto : entry.getValue()) {
				String key = dto.getSiteId() + "-" + dto.getProductCode();
				keyMap.put(key, dto);
				Set<String> codeSet = siteCodeMap.get(dto.getSiteId());
				if (codeSet == null) {
					codeSet = new HashSet<String>();
					siteCodeMap.put(dto.getSiteId(), codeSet);
				}
				codeSet.add(dto.getProductCode());
			}
			//
			for (Entry<Integer, Set<String>> sEntry : siteCodeMap.entrySet()) {
				Integer siteId = sEntry.getKey();
				List<String> codeList = new ArrayList<String>(sEntry.getValue());
				List<MatchDto> hasList = getMatchDtoByProductCodes(siteId, codeList);
			}
		}

	}

	@Override
	public List<MatchDto> getDtoByIds(List<Long> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return Collections.emptyList();
		}
		return matchDao.getDtoByIds(idList);
	}

	@Override
	public List<MatchDto> getMatchDtoByMatchCodes(List<Long> matchCodeList) {
		if (CollectionUtils.isEmpty(matchCodeList)) {
			return Collections.emptyList();
		}
		return matchDao.getMatchDtoByMatchCodes(matchCodeList);
	}

	@Override
	public List<MatchDto> getMatchDtoByProductCodes(Integer siteId, List<String> codeList) {
		if (CollectionUtils.isEmpty(codeList)) {
			return Collections.emptyList();
		}
		return matchDao.getMatchDtoByProductCodes(siteId, codeList);
	}

}
