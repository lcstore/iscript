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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProxyDetectDao;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ProxyDetectServiceImpl implements ProxyDetectService {
	private static Logger logger = LoggerFactory.getLogger(ProxyDetectServiceImpl.class);
	@Autowired
	private ProxyDetectDao proxyDetectDao;

	@Override
	public void batchInsertProxyDetectDtos(List<ProxyDetectDto> dtoList) {
		BatchIterator<ProxyDetectDto> it = new BatchIterator<ProxyDetectDto>(dtoList);
		while (it.hasNext()) {
			proxyDetectDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateProxyDetectDtos(List<ProxyDetectDto> dtoList) {
		BatchIterator<ProxyDetectDto> it = new BatchIterator<ProxyDetectDto>(dtoList);
		while (it.hasNext()) {
			proxyDetectDao.batchUpdate(dtoList);
		}
	}

	@Override
	public void batchInsertIfAbsent(List<ProxyDetectDto> dtoList) {
		List<ProxyDetectDto> insertDtos = new ArrayList<ProxyDetectDto>();
		doAbsentAssort(dtoList, insertDtos, null);
		batchInsertProxyDetectDtos(insertDtos);
		logger.info("save ProxyDetectDto.insert:" + insertDtos.size());

	}

	private void doAbsentAssort(List<ProxyDetectDto> dtoList, List<ProxyDetectDto> insertDtos,
			List<ProxyDetectDto> updateDtos) {
		Set<Long> ipSet = new HashSet<Long>();
		Set<Integer> portSet = new HashSet<Integer>();
		Map<String, ProxyDetectDto> dtoMap = new HashMap<String, ProxyDetectDto>();
		for (ProxyDetectDto dto : dtoList) {
			ipSet.add(dto.getIp());
			portSet.add(dto.getPort());
			String key = getDtoKey(dto);
			dtoMap.put(key, dto);
		}
		List<ProxyDetectDto> hasDtos = getProxyDetectDtos(new ArrayList<Long>(ipSet), new ArrayList<Integer>(portSet),
				null);
		Set<String> hasCodeSet = new HashSet<String>();
		for (ProxyDetectDto oldDto : hasDtos) {
			String key = getDtoKey(oldDto);
			hasCodeSet.add(key);
		}
		for (Entry<String, ProxyDetectDto> entry : dtoMap.entrySet()) {
			if (hasCodeSet.contains(entry.getKey())) {
				continue;
			}
			ProxyDetectDto newDto = entry.getValue();
			insertDtos.add(newDto);
		}

	}

	@Override
	public void batchSaveAfterDetect(List<ProxyDetectDto> dtoList) {
		List<ProxyDetectDto> insertDtos = new ArrayList<ProxyDetectDto>();
		List<ProxyDetectDto> updateDtos = new ArrayList<ProxyDetectDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchInsertProxyDetectDtos(insertDtos);
		batchUpdateProxyDetectDtos(updateDtos);
		logger.info("save ProxyDetectDto.insert:" + insertDtos.size() + ",update:" + updateDtos.size());
	}

	private void doAssort(List<ProxyDetectDto> dtoList, List<ProxyDetectDto> insertDtos, List<ProxyDetectDto> updateDtos) {
		Set<Long> ipSet = new HashSet<Long>();
		Set<Integer> portSet = new HashSet<Integer>();
		Map<String, ProxyDetectDto> dtoMap = new HashMap<String, ProxyDetectDto>();
		for (ProxyDetectDto dto : dtoList) {
			ipSet.add(dto.getIp());
			portSet.add(dto.getPort());
			String key = getDtoKey(dto);
			dtoMap.put(key, dto);
		}
		List<ProxyDetectDto> hasDtos = getProxyDetectDtos(new ArrayList<Long>(ipSet), new ArrayList<Integer>(portSet),
				null);
		Set<String> hasCodeSet = new HashSet<String>();
		for (ProxyDetectDto oldDto : hasDtos) {
			String key = getDtoKey(oldDto);
			hasCodeSet.add(key);
			ProxyDetectDto newDto = dtoMap.get(key);
			if (newDto == null) {
				continue;
			}
			if (newDto.getMaxCost() < oldDto.getMaxCost()) {
				newDto.setMaxCost(oldDto.getMaxCost());
			}
			if (newDto.getMinCost() > oldDto.getMinCost()) {
				newDto.setMinCost(oldDto.getMinCost());
			}
			if (ProxyDetectDto.STATUS_USABLE == newDto.getStatus()) {
				newDto.setRetryTimes(0);
			} else if (ProxyDetectDto.STATUS_RETRY == newDto.getStatus()) {
				newDto.setRetryTimes(oldDto.getRetryTimes() + 1);
				if (newDto.getRetryTimes() >= ProxyDetectDto.MAX_RETRY_TIMES) {
					newDto.setStatus(ProxyDetectDto.STATUS_NONUSE);
				}
			}
			newDto.setId(oldDto.getId());
			newDto.setCreateTime(oldDto.getCreateTime());
			updateDtos.add(newDto);
		}
		for (Entry<String, ProxyDetectDto> entry : dtoMap.entrySet()) {
			if (hasCodeSet.contains(entry.getKey())) {
				continue;
			}
			ProxyDetectDto newDto = entry.getValue();
			insertDtos.add(newDto);
		}
	}

	private String getDtoKey(ProxyDetectDto dto) {
		String key = dto.getIp() + "-" + dto.getPort() + "-" + dto.getDomain();
		return key;
	}

	@Override
	public List<ProxyDetectDto> getProxyDetectDtos(List<Long> ipLongs, List<Integer> portList, Integer status) {
		List<ProxyDetectDto> dtoList = new ArrayList<ProxyDetectDto>();
		BatchIterator<Long> it = new BatchIterator<Long>(ipLongs);
		while (it.hasNext()) {
			List<ProxyDetectDto> subList = proxyDetectDao.getProxyDetectDtos(it.next(), portList, status);
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	@Override
	public List<ProxyDetectDto> getProxyDetectDtosFromId(Long fromId, int limit, Integer status) {
		if (limit <= 0) {
			return Collections.emptyList();
		}
		return proxyDetectDao.getProxyDetectDtosFromId(fromId, limit, status);
	}

	public void setProxyDetectDao(ProxyDetectDao proxyDetectDao) {
		this.proxyDetectDao = proxyDetectDao;
	}

}
