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

import com.lezo.iscript.service.crawler.dao.ListRankDao;
import com.lezo.iscript.service.crawler.dto.ListRankDto;
import com.lezo.iscript.service.crawler.service.ListRankService;
import com.lezo.iscript.utils.BatchIterator;
@Service
public class ListRankServiceImpl implements ListRankService {
	@Autowired
	private ListRankDao listRankDao;

	@Override
	public void batchInsertDtos(List<ListRankDto> dtoList) {
		BatchIterator<ListRankDto> it = new BatchIterator<ListRankDto>(dtoList);
		while (it.hasNext()) {
			listRankDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateDtos(List<ListRankDto> dtoList) {
		BatchIterator<ListRankDto> it = new BatchIterator<ListRankDto>(dtoList);
		while (it.hasNext()) {
			listRankDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<ListRankDto> dtoList) {
		Map<String, List<ListRankDto>> categoryRankMap = getCategoryRankMap(dtoList);
		for (Entry<String, List<ListRankDto>> entry : categoryRankMap.entrySet()) {
			// delete exist
			Map<Integer, Set<String>> shopCodeMap = getShopCodeMap(entry.getValue());
			for (Entry<Integer, Set<String>> sEntry : shopCodeMap.entrySet()) {
				List<String> codeList = new ArrayList<String>(sEntry.getValue());
				deleteListRanks(entry.getKey(), codeList, sEntry.getKey());
			}
			// insert new
			batchInsertDtos(entry.getValue());
		}
	}

	private Map<Integer, Set<String>> getShopCodeMap(List<ListRankDto> dtoList) {
		Map<Integer, Set<String>> sMap = new HashMap<Integer, Set<String>>();
		for (ListRankDto dto : dtoList) {
			Set<String> codeSet = sMap.get(dto.getShopId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				sMap.put(dto.getShopId(), codeSet);
			}
			codeSet.add(dto.getProductCode());
		}
		return sMap;
	}

	private Map<String, List<ListRankDto>> getCategoryRankMap(List<ListRankDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return Collections.emptyMap();
		}
		Map<String, List<ListRankDto>> cMap = new HashMap<String, List<ListRankDto>>();
		for (ListRankDto dto : dtoList) {
			List<ListRankDto> rankDtos = cMap.get(dto.getCategoryName());
			if (rankDtos == null) {
				rankDtos = new ArrayList<ListRankDto>();
				cMap.put(dto.getCategoryName(), rankDtos);
			}
			rankDtos.add(dto);
		}
		return cMap;
	}

	@Override
	public List<ListRankDto> getListRankDtos(String categoryName, List<String> codeList, Integer shopId) {
		return listRankDao.getListRankDtos(categoryName, codeList, shopId);
	}

	public void setListRankDao(ListRankDao listRankDao) {
		this.listRankDao = listRankDao;
	}

	@Override
	public void deleteListRanks(String categoryName, List<String> codeList, Integer shopId) {
		BatchIterator<String> it = new BatchIterator<String>(codeList, 500);
		while (it.hasNext()) {
			listRankDao.deleteListRanks(categoryName, it.next(), shopId);
		}
	}
}
