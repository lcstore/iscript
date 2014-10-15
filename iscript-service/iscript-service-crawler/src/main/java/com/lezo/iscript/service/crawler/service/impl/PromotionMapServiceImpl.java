package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.PromotionMapDao;
import com.lezo.iscript.service.crawler.dto.PromotionMapDto;
import com.lezo.iscript.service.crawler.service.PromotionMapService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class PromotionMapServiceImpl implements PromotionMapService {
	@Autowired
	private PromotionMapDao promotionMapDao;

	@Override
	public void batchInsertDtos(List<PromotionMapDto> dtoList) {
		BatchIterator<PromotionMapDto> it = new BatchIterator<PromotionMapDto>(dtoList);
		while (it.hasNext()) {
			promotionMapDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateDtos(List<PromotionMapDto> dtoList) {
		BatchIterator<PromotionMapDto> it = new BatchIterator<PromotionMapDto>(dtoList);
		while (it.hasNext()) {
			promotionMapDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<PromotionMapDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		Map<String, PromotionMapDto> keyMap = convert2KeyMap(dtoList);
		Map<Integer, Map<String, List<PromotionMapDto>>> siteCodeDtosMap = new HashMap<Integer, Map<String, List<PromotionMapDto>>>();
		for (PromotionMapDto dto : keyMap.values()) {
			Map<String, List<PromotionMapDto>> codeDtosMap = siteCodeDtosMap.get(dto.getSiteId());
			if (codeDtosMap == null) {
				codeDtosMap = new HashMap<String, List<PromotionMapDto>>();
				siteCodeDtosMap.put(dto.getSiteId(), codeDtosMap);
			}
			List<PromotionMapDto> promoteList = codeDtosMap.get(dto.getProductCode());
			if (promoteList == null) {
				promoteList = new ArrayList<PromotionMapDto>();
				codeDtosMap.put(dto.getProductCode(), promoteList);
			}
			promoteList.add(dto);
		}
		for (Entry<Integer, Map<String, List<PromotionMapDto>>> entry : siteCodeDtosMap.entrySet()) {
			List<String> productCodes = new ArrayList<String>(entry.getValue().keySet());
			List<PromotionMapDto> hasDtos = getPromotionMapDtosByProductCodes(entry.getKey(), productCodes, null, null,
					PromotionMapDto.DELETE_FALSE);
			List<PromotionMapDto> updateList = new ArrayList<PromotionMapDto>(hasDtos.size());
			List<PromotionMapDto> insertList = new ArrayList<PromotionMapDto>(productCodes.size());
			doAssort(hasDtos, entry.getValue(), keyMap, updateList, insertList);
			// make old promotion delete
			Set<Long> hasIdSet = convert2Set(hasDtos);
			batchUpdateIsDelete(new ArrayList<Long>(hasIdSet), PromotionMapDto.DELETE_TRUE);
			batchUpdateDtos(updateList);
			batchInsertDtos(insertList);
		}

	}

	private Set<Long> convert2Set(List<PromotionMapDto> hasDtos) {
		Set<Long> hasIdSet = new HashSet<Long>(hasDtos.size());
		for (PromotionMapDto oldDto : hasDtos) {
			hasIdSet.add(oldDto.getId());
		}
		return hasIdSet;
	}

	private void doAssort(List<PromotionMapDto> hasDtos, Map<String, List<PromotionMapDto>> codeDtosMap,
			Map<String, PromotionMapDto> keyMap, List<PromotionMapDto> updateList, List<PromotionMapDto> insertList) {
		Set<String> hasKeySet = new HashSet<String>(hasDtos.size());
		for (PromotionMapDto oldDto : hasDtos) {
			String key = getKey(oldDto);
			PromotionMapDto newDto = keyMap.get(key);
			if (newDto != null) {
				hasKeySet.add(key);
				newDto.setId(oldDto.getId());
				newDto.setCreateTime(oldDto.getCreateTime());
				updateList.add(newDto);
			}
		}
		for (Entry<String, List<PromotionMapDto>> cEntry : codeDtosMap.entrySet()) {
			for (PromotionMapDto dto : cEntry.getValue()) {
				String key = getKey(dto);
				if (hasKeySet.contains(key)) {
					continue;
				}
				if (!StringUtils.isEmpty(dto.getPromoteCode())) {
					insertList.add(dto);
				}
			}
		}

	}

	private Map<String, PromotionMapDto> convert2KeyMap(List<PromotionMapDto> dtoList) {
		Map<String, PromotionMapDto> keyMap = new HashMap<String, PromotionMapDto>(dtoList.size());
		for (PromotionMapDto dto : dtoList) {
			String key = getKey(dto);
			PromotionMapDto hasDto = keyMap.get(key);
			if (hasDto == null || hasDto.getUpdateTime().before(dto.getUpdateTime())) {
				keyMap.put(key, dto);
			}
		}
		return keyMap;
	}

	private String getKey(PromotionMapDto dto) {
		StringBuilder sb = new StringBuilder();
		sb.append(dto.getSiteId());
		sb.append("-");
		sb.append(dto.getProductCode());
		sb.append("-");
		sb.append(dto.getPromoteCode());
		return sb.toString();
	}

	@Override
	public List<PromotionMapDto> getPromotionMapDtosByProductCodes(Integer siteId, List<String> productCodes,
			Integer promoteType, Integer promoteStatus, Integer isDelete) {
		List<PromotionMapDto> resultList = new ArrayList<PromotionMapDto>();
		BatchIterator<String> it = new BatchIterator<String>(productCodes);
		while (it.hasNext()) {
			List<PromotionMapDto> blockList = promotionMapDao.getPromotionMapDtosByProductCodes(siteId, it.next(),
					promoteType, promoteStatus, isDelete);
			if (!blockList.isEmpty()) {
				resultList.addAll(blockList);
			}
		}
		return resultList;
	}

	@Override
	public List<PromotionMapDto> getPromotionMapDtosByPromotCodes(Integer siteId, List<String> promotCodes,
			Integer promoteType, Integer promoteStatus, Integer isDelete) {
		List<PromotionMapDto> resultList = new ArrayList<PromotionMapDto>();
		BatchIterator<String> it = new BatchIterator<String>(promotCodes);
		while (it.hasNext()) {
			List<PromotionMapDto> blockList = promotionMapDao.getPromotionMapDtosByPromotCodes(siteId, it.next(),
					promoteType, promoteStatus, isDelete);
			if (!blockList.isEmpty()) {
				resultList.addAll(blockList);
			}
		}
		return resultList;
	}

	@Override
	public void batchUpdateIsDelete(List<Long> idList, Integer isDelete) {
		if (CollectionUtils.isEmpty(idList)) {
			return;
		}
		promotionMapDao.batchUpdateIsDelete(idList, isDelete);
	}

	public void setPromotionMapDao(PromotionMapDao promotionMapDao) {
		this.promotionMapDao = promotionMapDao;
	}

}
