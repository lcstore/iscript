package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
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
		List<ListRankDto> insertDtos = new ArrayList<ListRankDto>();
		List<ListRankDto> updateDtos = new ArrayList<ListRankDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchInsertDtos(insertDtos);
		batchUpdateDtos(updateDtos);
		logger.info(String.format("save [%s],insert:%d,update:%d,cost:", "ListRankDto", insertDtos.size(),
				updateDtos.size()));
	}

	private Map<Integer, Set<String>> getShopCodeMap(List<ListRankDto> dtoList) {
		Map<Integer, Set<String>> sMap = new HashMap<Integer, Set<String>>();
	private void doAssort(List<ListRankDto> dtoList, List<ListRankDto> insertDtos, List<ListRankDto> updateDtos) {
		Map<String, Map<Integer, Set<String>>> urlSiteCodeMap = new HashMap<String, Map<Integer, Set<String>>>();
		Map<String, ListRankDto> dtoMap = new HashMap<String, ListRankDto>();
		for (ListRankDto dto : dtoList) {
			Set<String> codeSet = sMap.get(dto.getShopId());
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
				sMap.put(dto.getShopId(), codeSet);
				siteCodeMap.put(dto.getShopId(), codeSet);
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
			rankDtos.add(dto);
		}
		return cMap;

	}

	private String getDtoKey(ListRankDto dto) {
		return dto.getShopId() + "-" + dto.getProductCode() + "-" + dto.getListUrl();
	}

	@Override
	public List<ListRankDto> getListRankDtos(String categoryName, List<String> codeList, Integer shopId) {
		return listRankDao.getListRankDtos(categoryName, codeList, shopId);
	public List<ListRankDto> getListRankDtos(String listUrl, List<String> codeList, Integer shopId) {
		if (shopId == null) {
			return Collections.emptyList();
		}
		return listRankDao.getListRankDtos(listUrl, codeList, shopId);
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