package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.BrandShopDao;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;
import com.lezo.iscript.service.crawler.dto.BrandShopDto;
import com.lezo.iscript.service.crawler.service.BrandShopService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class BrandShopServiceImpl implements BrandShopService {
	@Autowired
	private BrandShopDao brandShopDao;

	@Override
	public void batchInsertDtos(List<BrandShopDto> dtoList) {
		BatchIterator<BrandShopDto> it = new BatchIterator<BrandShopDto>(dtoList);
		while (it.hasNext()) {
			brandShopDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateDtos(List<BrandShopDto> dtoList) {
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
			List<BrandShopDto> hasList = getBrandShopDtoByShopNameList(new ArrayList<String>(codeSet), new ArrayList<String>(nameSet), entry.getKey());
			Set<String> hasSet = new HashSet<String>();
			for (BrandShopDto oldDto : hasList) {
				String key = oldDto.getBrandCode() + "-" + oldDto.getBrandName();
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

	@Override
	public List<BrandShopDto> getBrandShopDtoByShopNameList(List<String> shopNameList, List<String> brandCodeList, Integer siteId) {
		if (CollectionUtils.isEmpty(shopNameList)) {
			return Collections.emptyList();
		}
		return this.brandShopDao.getBrandShopDtoByShopNameList(shopNameList, brandCodeList, siteId);
	}

}
