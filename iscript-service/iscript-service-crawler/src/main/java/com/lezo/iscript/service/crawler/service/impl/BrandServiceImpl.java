package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.BrandDao;
import com.lezo.iscript.service.crawler.dto.BrandDto;
import com.lezo.iscript.service.crawler.service.BrandService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class BrandServiceImpl implements BrandService {
	@Autowired
	private BrandDao brandDao;

	@Override
	public void batchInsertDtos(List<BrandDto> dtoList) {
		convertNullToDefault(dtoList);
		BatchIterator<BrandDto> it = new BatchIterator<BrandDto>(dtoList);
		while (it.hasNext()) {
			brandDao.batchInsert(it.next());
		}

	}

	private void convertNullToDefault(List<BrandDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		for (BrandDto dto : dtoList) {
			if (dto.getBrandCode() == null) {
				dto.setBrandCode(StringUtils.EMPTY);
			}
			if (dto.getBrandName() == null) {
				dto.setBrandName(StringUtils.EMPTY);
			}
			if (dto.getBrandUrl() == null) {
				dto.setBrandUrl(StringUtils.EMPTY);
			}
			if (dto.getSynonymCode() == null) {
				dto.setSynonymCode(StringUtils.EMPTY);
			}
			if (dto.getRegion() == null) {
				dto.setRegion(StringUtils.EMPTY);
			}
		}
	}

	@Override
	public void batchUpdateDtos(List<BrandDto> dtoList) {
		convertNullToDefault(dtoList);
		BatchIterator<BrandDto> it = new BatchIterator<BrandDto>(dtoList);
		while (it.hasNext()) {
			brandDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<BrandDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		List<BrandDto> updateList = new ArrayList<BrandDto>();
		List<BrandDto> insertList = new ArrayList<BrandDto>();
		doAssort(dtoList, updateList, insertList);
		batchInsertDtos(insertList);
		batchUpdateDtos(updateList);
	}

	private void doAssort(List<BrandDto> dtoList, List<BrandDto> updateList, List<BrandDto> insertList) {
		Map<String, List<BrandDto>> synCodeMap = toSameSynCode(dtoList);
		for (Entry<String, List<BrandDto>> entry : synCodeMap.entrySet()) {
			Set<String> codeSet = new HashSet<String>();
			Set<String> nameSet = new HashSet<String>();
			Map<String, BrandDto> codeNameMap = new HashMap<String, BrandDto>();
			for (BrandDto dto : entry.getValue()) {
				codeSet.add(dto.getBrandCode());
				nameSet.add(dto.getBrandName());
				String key = dto.getBrandCode() + "-" + dto.getBrandName();
				codeNameMap.put(key, dto);
			}
			List<BrandDto> hasList = brandDao.getBrandDtoByCodes(new ArrayList<String>(codeSet), new ArrayList<String>(nameSet), null);
			Set<String> hasSet = new HashSet<String>();
			for (BrandDto oldDto : hasList) {
				String key = oldDto.getBrandCode() + "-" + oldDto.getBrandName();
				BrandDto newDto = codeNameMap.get(key);
				if (newDto != null) {
					newDto.setId(oldDto.getId());
					hasSet.add(key);
					updateList.add(newDto);
				}
			}
			BrandDto oldBaseDto = hasList.isEmpty() ? null : hasList.get(0);
			for (Entry<String, BrandDto> cnEntry : codeNameMap.entrySet()) {
				if (hasSet.contains(cnEntry.getKey())) {
					continue;
				}
				if (oldBaseDto != null) {
					cnEntry.getValue().setSynonymCode(oldBaseDto.getSynonymCode());
				}
				insertList.add(cnEntry.getValue());
			}
		}
	}

	private Map<String, List<BrandDto>> toSameSynCode(List<BrandDto> dtoList) {
		Map<String, List<BrandDto>> synCodeMap = new HashMap<String, List<BrandDto>>();
		for (BrandDto dto : dtoList) {
			List<BrandDto> dataList = synCodeMap.get(dto.getSynonymCode());
			if (dataList == null) {
				dataList = new ArrayList<BrandDto>();
				synCodeMap.put(dto.getSynonymCode(), dataList);
			}
			dataList.add(dto);
		}
		Map<String, Set<String>> synNameMap = new HashMap<String, Set<String>>();
		for (BrandDto dto : dtoList) {
			Set<String> nameSet = synNameMap.get(dto.getSynonymCode());
			if (nameSet == null) {
				nameSet = new HashSet<String>();
				synNameMap.put(dto.getSynonymCode(), nameSet);
			}
			nameSet.add(dto.getBrandName());
		}
		List<String> synCodeList = new ArrayList<String>(synNameMap.keySet());
		Collections.sort(synCodeList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		for (String synCode : synCodeList) {
			Set<String> nameSet = synNameMap.get(synCode);
			if (nameSet == null) {
				continue;
			}
			Iterator<Entry<String, Set<String>>> it = synNameMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Set<String>> inEntry = it.next();
				if (synCode.equals(inEntry.getKey())) {
					continue;
				}
				boolean bSame = false;
				for (String brandName : inEntry.getValue()) {
					if (nameSet.contains(brandName)) {
						bSame = true;
						break;
					}
				}
				if (bSame) {
					nameSet.addAll(inEntry.getValue());
					List<BrandDto> brandList = synCodeMap.get(synCode);
					List<BrandDto> sameList = synCodeMap.get(inEntry.getKey());
					if (sameList != null) {
						for (BrandDto sameDto : sameList) {
							sameDto.setSynonymCode(synCode);
						}
						brandList.addAll(sameList);
					}
					it.remove();
					synCodeMap.remove(inEntry.getKey());
				}
			}
		}
		return synCodeMap;
	}

	@Override
	public void saveBrandStoreDtoAndGetId(BrandDto dto) {
		if (dto == null) {
			return;
		}
		this.brandDao.insertBrandStoreDtoAndSetId(dto);
	}

	@Override
	public List<BrandDto> getBrandStoreDtoByCodes(List<String> brandCodeList, List<String> brandNameList, Integer siteId) {
		if (CollectionUtils.isEmpty(brandCodeList)) {
			return Collections.emptyList();
		}
		return this.brandDao.getBrandDtoByCodes(brandCodeList, brandNameList, siteId);
	}

	@Override
	public List<BrandDto> getBrandStoreDtoByIds(List<Long> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return Collections.emptyList();
		}
		return this.brandDao.getBrandDtoByIds(idList);
	}

}
