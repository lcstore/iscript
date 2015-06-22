package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProxySeedDao;
import com.lezo.iscript.service.crawler.dto.ProxySeedDto;
import com.lezo.iscript.service.crawler.service.ProxySeedService;
import com.lezo.iscript.utils.BatchIterator;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年2月5日
 */
@Service
public class ProxySeedServiceImpl implements ProxySeedService {
	@Autowired
	private ProxySeedDao proxySeedDao;

	@Override
	public int batchInsertDtos(List<ProxySeedDto> dtoList) {
		int affect = 0;
		BatchIterator<ProxySeedDto> it = new BatchIterator<ProxySeedDto>(dtoList);
		while (it.hasNext()) {
			affect += proxySeedDao.batchInsert(it.next());
		}
		return affect;
	}

	@Override
	public int batchUpdateDtos(List<ProxySeedDto> dtoList) {
		int affect = 0;
		BatchIterator<ProxySeedDto> it = new BatchIterator<ProxySeedDto>(dtoList);
		while (it.hasNext()) {
			affect += proxySeedDao.batchUpdate(it.next());
		}
		return affect;
	}

	@Override
	public int batchSaveDtos(List<ProxySeedDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return 0;
		}
		int affect = 0;
		List<ProxySeedDto> insertList = new ArrayList<ProxySeedDto>();
		List<ProxySeedDto> updateList = new ArrayList<ProxySeedDto>();
		doAssort(dtoList, insertList, updateList);
		affect += batchUpdateDtos(dtoList);
		affect += batchInsertDtos(dtoList);
		return affect;
	}

	private void doAssort(List<ProxySeedDto> dtoList, List<ProxySeedDto> insertList, List<ProxySeedDto> updateList) {
		List<ProxySeedDto> allList = getProxySeedDtoByFromId(0L, Integer.MAX_VALUE);
		Map<String, ProxySeedDto> url2DtoMap = new HashMap<String, ProxySeedDto>();
		for (ProxySeedDto dto : allList) {
			url2DtoMap.put(dto.getUrl(), dto);
		}
		for (ProxySeedDto newDto : dtoList) {
			ProxySeedDto oldDto = url2DtoMap.get(newDto.getUrl());
			if (oldDto == null) {
				insertList.add(newDto);
			} else {
				newDto.setId(oldDto.getId());
				updateList.add(newDto);
			}
		}

	}

	@Override
	public List<ProxySeedDto> getProxySeedDtoByFromId(Long fromId, int limit) {
		if (limit <= 0) {
			return Collections.emptyList();
		}
		return proxySeedDao.getProxySeedDtoByFromId(fromId, limit);
	}

	public void setProxySeedDao(ProxySeedDao proxySeedDao) {
		this.proxySeedDao = proxySeedDao;
	}

}
