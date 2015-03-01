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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.BarCodeItemDao;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.service.BarCodeItemService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class BarCodeItemServiceImpl implements BarCodeItemService {
	private static Logger logger = LoggerFactory.getLogger(BarCodeItemServiceImpl.class);
	@Autowired
	private BarCodeItemDao barCodeItemDao;

	@Override
	public void batchInsertBarCodeItemDtos(List<BarCodeItemDto> dtoList) {
		BatchIterator<BarCodeItemDto> it = new BatchIterator<BarCodeItemDto>(dtoList);
		while (it.hasNext()) {
			barCodeItemDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateBarCodeItemDtos(List<BarCodeItemDto> dtoList) {
		BatchIterator<BarCodeItemDto> it = new BatchIterator<BarCodeItemDto>(dtoList);
		while (it.hasNext()) {
			barCodeItemDao.batchUpdate(it.next());
		}
	}

	@Override
	public List<BarCodeItemDto> getBarCodeItemDtos(List<String> barCodeList) {
		List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
		BatchIterator<String> it = new BatchIterator<String>(barCodeList);
		while (it.hasNext()) {
			List<BarCodeItemDto> subList = barCodeItemDao.getBarCodeItemDtos(it.next());
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	public void setBarCodeItemDao(BarCodeItemDao barCodeItemDao) {
		this.barCodeItemDao = barCodeItemDao;
	}

	@Override
	public List<BarCodeItemDto> getBarCodeItemDtoFromId(Long fromId, int limit, String cateName) {
		if (limit < 1) {
			return Collections.emptyList();
		}
		return barCodeItemDao.getBarCodeItemDtoFromId(fromId, limit, cateName);
	}

	@Override
	public void batchSaveBarCodeItemDtos(List<BarCodeItemDto> dtoList) {
		List<BarCodeItemDto> insertDtos = new ArrayList<BarCodeItemDto>();
		List<BarCodeItemDto> updateDtos = new ArrayList<BarCodeItemDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchInsertBarCodeItemDtos(insertDtos);
		batchUpdateBarCodeItemDtos(updateDtos);
		logger.info("save BarCodeItemDto.insert:" + insertDtos.size() + ",update:" + updateDtos.size());
	}

	private void doAssort(List<BarCodeItemDto> barCodeItemDtos, List<BarCodeItemDto> insertDtos,
			List<BarCodeItemDto> updateDtos) {
		Map<String, BarCodeItemDto> dtoMap = new HashMap<String, BarCodeItemDto>();
		for (BarCodeItemDto dto : barCodeItemDtos) {
			String key = dto.getBarCode();
			dtoMap.put(key, dto);
		}
		List<String> barCodeList = new ArrayList<String>(dtoMap.keySet());

		List<BarCodeItemDto> hasDtos = getBarCodeItemDtos(barCodeList);
		Set<String> hasCodeSet = new HashSet<String>();
		for (BarCodeItemDto oldDto : hasDtos) {
			String key = oldDto.getBarCode();
			hasCodeSet.add(key);
		}
		for (Entry<String, BarCodeItemDto> entry : dtoMap.entrySet()) {
			if (hasCodeSet.contains(entry.getKey())) {
				continue;
			}
			BarCodeItemDto newDto = entry.getValue();
			insertDtos.add(newDto);
		}
	}
}
