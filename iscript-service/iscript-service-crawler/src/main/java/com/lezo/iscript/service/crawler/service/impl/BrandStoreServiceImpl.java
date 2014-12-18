package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
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

import com.lezo.iscript.service.crawler.dao.BrandStoreDao;
import com.lezo.iscript.service.crawler.dto.BrandStoreDto;
import com.lezo.iscript.service.crawler.service.BrandStoreService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class BrandStoreServiceImpl implements BrandStoreService {
	@Autowired
	private BrandStoreDao brandStoreDao;

	@Override
	public void batchInsertDtos(List<BrandStoreDto> dtoList) {
		BatchIterator<BrandStoreDto> it = new BatchIterator<BrandStoreDto>(dtoList);
		while (it.hasNext()) {
			brandStoreDao.batchInsert(it.next());
		}

	}

	@Override
	public void batchUpdateDtos(List<BrandStoreDto> dtoList) {
		BatchIterator<BrandStoreDto> it = new BatchIterator<BrandStoreDto>(dtoList);
		while (it.hasNext()) {
			brandStoreDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<BrandStoreDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		List<BrandStoreDto> updateList = new ArrayList<BrandStoreDto>();
		List<BrandStoreDto> insertList = new ArrayList<BrandStoreDto>();
		doAssort(dtoList, updateList, insertList);
		batchInsertDtos(insertList);
		batchUpdateDtos(updateList);
	}

	private void doAssort(List<BrandStoreDto> dtoList, List<BrandStoreDto> updateList, List<BrandStoreDto> insertList) {
		Map<Integer, List<BrandStoreDto>> siteMap = new HashMap<Integer, List<BrandStoreDto>>();
		for (BrandStoreDto dto : dtoList) {
			List<BrandStoreDto> dataList = siteMap.get(dto.getSiteId());
			if (dataList == null) {
				dataList = new ArrayList<BrandStoreDto>();
				siteMap.put(dto.getSiteId(), dataList);
			}
			dataList.add(dto);
		}

		for (Entry<Integer, List<BrandStoreDto>> entry : siteMap.entrySet()) {
			Set<String> codeSet = new HashSet<String>();
			Set<String> nameSet = new HashSet<String>();
			Map<String, BrandStoreDto> codeNameMap = new HashMap<String, BrandStoreDto>();
			for (BrandStoreDto dto : entry.getValue()) {
				codeSet.add(dto.getBrandCode());
				nameSet.add(dto.getBrandName());
				String key = dto.getBrandCode() + "-" + dto.getBrandName();
				codeNameMap.put(key, dto);
			}
			List<BrandStoreDto> hasList = brandStoreDao.getBrandStoreDtoByCodes(new ArrayList<String>(codeSet), new ArrayList<String>(nameSet), entry.getKey());
			Set<String> hasSet = new HashSet<String>();
			for (BrandStoreDto oldDto : hasList) {
				String key = oldDto.getBrandCode() + "-" + oldDto.getBrandName();
				BrandStoreDto newDto = codeNameMap.get(key);
				if (newDto != null) {
					newDto.setId(oldDto.getId());
					hasSet.add(key);
					updateList.add(newDto);
				}
			}
			for (Entry<String, BrandStoreDto> cnEntry : codeNameMap.entrySet()) {
				if (hasSet.contains(cnEntry.getKey())) {
					continue;
				}
				insertList.add(cnEntry.getValue());
			}
		}
	}

	@Override
	public void saveBrandStoreDtoAndGetId(BrandStoreDto dto) {
		if (dto == null) {
			return;
		}
		this.brandStoreDao.insertBrandStoreDtoAndSetId(dto);
	}

	@Override
	public List<BrandStoreDto> getBrandStoreDtoByCodes(List<String> brandCodeList, List<String> brandNameList, Integer siteId) {
		if (CollectionUtils.isEmpty(brandCodeList)) {
			return Collections.emptyList();
		}
		return this.brandStoreDao.getBrandStoreDtoByCodes(brandCodeList, brandNameList, siteId);
	}

	@Override
	public List<BrandStoreDto> getBrandStoreDtoByIds(List<Long> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return Collections.emptyList();
		}
		return this.brandStoreDao.getBrandStoreDtoByIds(idList);
	}

}
