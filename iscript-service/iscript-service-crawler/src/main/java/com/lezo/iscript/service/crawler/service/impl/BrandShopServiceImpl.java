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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.BrandShopDao;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;
import com.lezo.iscript.service.crawler.service.BrandShopService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class BrandShopServiceImpl implements BrandShopService {
	@Autowired
	private BrandShopDao brandShopDao;

	@Override
	public void batchInsertDtos(List<BrandShopDto> dtoList) {
		convertNullToDefault(dtoList);
		BatchIterator<BrandShopDto> it = new BatchIterator<BrandShopDto>(dtoList);
		while (it.hasNext()) {
			brandShopDao.batchInsert(it.next());
		}
	}

	private void convertNullToDefault(List<BrandShopDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		for (BrandShopDto dto : dtoList) {
			if (dto.getBrandCode() == null) {
				dto.setBrandCode(StringUtils.EMPTY);
			}
			if (dto.getBrandName() == null) {
				dto.setBrandName(StringUtils.EMPTY);
			}
			if (dto.getShopName() == null) {
				dto.setShopName(StringUtils.EMPTY);
			}
			if (dto.getShopCode() == null) {
				dto.setShopCode(StringUtils.EMPTY);
			}
			if (dto.getShopUrl() == null) {
				dto.setShopUrl(StringUtils.EMPTY);
			}
		}
	}

	@Override
	public void batchUpdateDtos(List<BrandShopDto> dtoList) {
		convertNullToDefault(dtoList);
		BatchIterator<BrandShopDto> it = new BatchIterator<BrandShopDto>(dtoList);
		while (it.hasNext()) {
			brandShopDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<BrandShopDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		List<BrandShopDto> updateList = new ArrayList<BrandShopDto>();
		List<BrandShopDto> insertList = new ArrayList<BrandShopDto>();
		doAssort(dtoList, updateList, insertList);
		batchInsertDtos(insertList);
		batchUpdateDtos(updateList);

	}

	private void doAssort(List<BrandShopDto> dtoList, List<BrandShopDto> updateList, List<BrandShopDto> insertList) {
		dtoList = removeDumplcation(dtoList);
		Map<Integer, List<BrandShopDto>> siteMap = new HashMap<Integer, List<BrandShopDto>>();
		for (BrandShopDto dto : dtoList) {
			List<BrandShopDto> dataList = siteMap.get(dto.getSiteId());
			if (dataList == null) {
				dataList = new ArrayList<BrandShopDto>();
				siteMap.put(dto.getSiteId(), dataList);
			}
			dataList.add(dto);
		}
		for (Entry<Integer, List<BrandShopDto>> entry : siteMap.entrySet()) {
			Set<String> codeSet = new HashSet<String>();
			Set<String> nameSet = new HashSet<String>();
			Map<String, BrandShopDto> codeNameMap = new HashMap<String, BrandShopDto>();
			for (BrandShopDto dto : entry.getValue()) {
				codeSet.add(dto.getBrandCode());
				nameSet.add(dto.getShopName());
				String key = dto.getBrandCode() + "-" + dto.getShopName();
				codeNameMap.put(key, dto);
			}
			List<BrandShopDto> hasList = getBrandShopDtoByShopNameList(new ArrayList<String>(nameSet), new ArrayList<String>(codeSet), entry.getKey());
			Set<String> hasSet = new HashSet<String>();
			for (BrandShopDto oldDto : hasList) {
				String key = oldDto.getBrandCode() + "-" + oldDto.getShopName();
				BrandShopDto newDto = codeNameMap.get(key);
				if (newDto != null) {
					newDto.setId(oldDto.getId());
					hasSet.add(key);
					updateList.add(newDto);
				}
			}
			for (Entry<String, BrandShopDto> cnEntry : codeNameMap.entrySet()) {
				if (hasSet.contains(cnEntry.getKey())) {
					continue;
				}
				insertList.add(cnEntry.getValue());
			}
		}

	}

	private List<BrandShopDto> removeDumplcation(List<BrandShopDto> dtoList) {
		List<BrandShopDto> resultList = new ArrayList<BrandShopDto>(dtoList.size());
		Set<String> hasKeySet = new HashSet<String>();
		for (BrandShopDto shopDto : dtoList) {
			String key = getDtoKey(shopDto);
			if (!hasKeySet.contains(key)) {
				hasKeySet.add(key);
				resultList.add(shopDto);
			}
		}
		return resultList;
	}

	private String getDtoKey(BrandShopDto shopDto) {
		return shopDto.getBrandCode() + "-" + shopDto.getShopCode() + "-" + shopDto.getSiteId();
	}

	@Override
	public List<BrandShopDto> getBrandShopDtoByShopNameList(List<String> shopNameList, List<String> brandCodeList, Integer siteId) {
		if (CollectionUtils.isEmpty(shopNameList)) {
			return Collections.emptyList();
		}
		return this.brandShopDao.getBrandShopDtoByShopNameList(shopNameList, brandCodeList, siteId);
	}

}
