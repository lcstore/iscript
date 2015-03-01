package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ListRankDao;
import com.lezo.iscript.service.crawler.dto.ListRankDto;
import com.lezo.iscript.service.crawler.service.ListRankService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ListRankServiceImpl implements ListRankService {
	private static Logger logger = LoggerFactory.getLogger(ListRankServiceImpl.class);

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
		List<ListRankDto> insertDtos = new ArrayList<ListRankDto>();
		List<ListRankDto> updateDtos = new ArrayList<ListRankDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchInsertDtos(insertDtos);
		batchUpdateDtos(updateDtos);
		logger.info(String.format("save [%s],insert:%d,update:%d,cost:", "ListRankDto", insertDtos.size(),
				updateDtos.size()));
	}

	private void doAssort(List<ListRankDto> dtoList, List<ListRankDto> insertDtos, List<ListRankDto> updateDtos) {
		Map<String, Map<Integer, Set<String>>> urlSiteCodeMap = new HashMap<String, Map<Integer, Set<String>>>();
		Map<String, ListRankDto> dtoMap = new HashMap<String, ListRankDto>();
		for (ListRankDto dto : dtoList) {
			String key = getDtoKey(dto);
			ListRankDto hasDto = dtoMap.get(key);
			if (hasDto == null) {
				dtoMap.put(key, dto);
			} else if (hasDto.getUpdateTime().before(dto.getUpdateTime())) {
				dtoMap.put(key, dto);
			}
			Map<Integer, Set<String>> siteCodeMap = urlSiteCodeMap.get(dto.getListUrl());
			if (siteCodeMap == null) {
				siteCodeMap = new HashMap<Integer, Set<String>>();
				urlSiteCodeMap.put(dto.getListUrl(), siteCodeMap);
			}
			Set<String> codeSet = siteCodeMap.get(dto.getShopId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				siteCodeMap.put(dto.getShopId(), codeSet);
			}
			codeSet.add(dto.getProductCode());
		}
		for (Entry<String, Map<Integer, Set<String>>> entry : urlSiteCodeMap.entrySet()) {
			for (Entry<Integer, Set<String>> cEntry : entry.getValue().entrySet()) {
				List<String> codeList = new ArrayList<String>(cEntry.getValue());
				List<ListRankDto> hasDtos = getListRankDtos(entry.getKey(), codeList, cEntry.getKey());
				Set<String> hasCodeSet = new HashSet<String>();
				for (ListRankDto dto : hasDtos) {
					String key = getDtoKey(dto);
					ListRankDto newDto = dtoMap.get(key);
					hasCodeSet.add(dto.getProductCode());
					newDto.setId(dto.getId());
					updateDtos.add(newDto);
				}
				for (String code : cEntry.getValue()) {
					if (hasCodeSet.contains(code)) {
						continue;
					}
					String key = cEntry.getKey() + "-" + code + "-" + entry.getKey();
					ListRankDto newDto = dtoMap.get(key);
					insertDtos.add(newDto);
				}
			}
		}

	}

	private String getDtoKey(ListRankDto dto) {
		return dto.getShopId() + "-" + dto.getProductCode() + "-" + dto.getListUrl();
	}

	@Override
	public List<ListRankDto> getListRankDtos(String listUrl, List<String> codeList, Integer shopId) {
		return listRankDao.getListRankDtos(listUrl, codeList, shopId);
	}

	public void setListRankDao(ListRankDao listRankDao) {
		this.listRankDao = listRankDao;
	}

}
