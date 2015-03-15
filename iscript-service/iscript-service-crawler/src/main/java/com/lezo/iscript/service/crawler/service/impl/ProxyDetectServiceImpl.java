package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.common.UnifyValueUtils;
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
		ensureAddrCodeFilled(dtoList);
		BatchIterator<ProxyDetectDto> it = new BatchIterator<ProxyDetectDto>(dtoList);
		while (it.hasNext()) {
			proxyDetectDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateProxyDetectDtos(List<ProxyDetectDto> dtoList) {
		ensureAddrCodeFilled(dtoList);
		BatchIterator<ProxyDetectDto> it = new BatchIterator<ProxyDetectDto>(dtoList);
		while (it.hasNext()) {
			proxyDetectDao.batchUpdate(it.next());
		}
	}

	private void ensureAddrCodeFilled(List<ProxyDetectDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		if (StringUtils.isNotEmpty(dtoList.get(0).getAddrCode())) {
			return;
		}
		for (ProxyDetectDto dto : dtoList) {
			String addrCode = getAddrCode(dto);
			dto.setAddrCode(addrCode);
		}
	}

	private String getAddrCode(ProxyDetectDto dto) {
		return dto.getIp() + "" + dto.getPort();
	}

	@Override
	public void batchInsertIfAbsent(List<ProxyDetectDto> dtoList) {
		ensureAddrCodeFilled(dtoList);
		UnifyValueUtils.unifyQuietly(dtoList);
		List<ProxyDetectDto> insertDtos = new ArrayList<ProxyDetectDto>();
		doAbsentAssort(dtoList, insertDtos);
		batchInsertProxyDetectDtos(insertDtos);
		logger.info("save ProxyDetectDto.batchInsertIfAbsent:{},total:{}", insertDtos.size(), dtoList.size());
	}

	private void doAbsentAssort(List<ProxyDetectDto> dtoList, List<ProxyDetectDto> insertDtos) {
		Map<String, List<ProxyDetectDto>> domainDetectMap = new HashMap<String, List<ProxyDetectDto>>();
		for (ProxyDetectDto dto : dtoList) {
			List<ProxyDetectDto> domainList = domainDetectMap.get(dto.getDomain());
			if (domainList == null) {
				domainList = new ArrayList<ProxyDetectDto>();
				domainDetectMap.put(dto.getDomain(), domainList);
			}
			domainList.add(dto);

		}
		for (Entry<String, List<ProxyDetectDto>> entry : domainDetectMap.entrySet()) {
			Set<String> codeSet = new HashSet<String>();
			for (ProxyDetectDto detectDto : entry.getValue()) {
				codeSet.add(detectDto.getAddrCode());
			}
			String domain = entry.getKey();
			List<String> addrCodeList = new ArrayList<String>(codeSet);
			List<ProxyDetectDto> hasDtos = getProxyDetectDtosByCodeList(addrCodeList, domain, null);
			Set<String> hasCodeSet = new HashSet<String>();
			for (ProxyDetectDto oldDto : hasDtos) {
				String key = getDtoKey(oldDto);
				hasCodeSet.add(key);
			}
			for (ProxyDetectDto newDto : entry.getValue()) {
				String key = getDtoKey(newDto);
				if (hasCodeSet.contains(key)) {
					continue;
				}
				if (1 == newDto.getStatus()) {
					newDto.setSuccessCount(1);
					newDto.setLastSuccessCount(1);
				} else if (0 == newDto.getStatus()) {
					newDto.setFailCount(1);
				}
				newDto.setMaxCost(newDto.getCurCost());
				newDto.setMinCost(newDto.getCurCost());
				insertDtos.add(newDto);
			}
		}
	}

	@Override
	public void batchSaveProxyDetectDtos(List<ProxyDetectDto> dtoList) {
		ensureAddrCodeFilled(dtoList);
		UnifyValueUtils.unifyQuietly(dtoList);
		List<ProxyDetectDto> insertDtos = new ArrayList<ProxyDetectDto>();
		List<ProxyDetectDto> updateDtos = new ArrayList<ProxyDetectDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchInsertProxyDetectDtos(insertDtos);
		batchUpdateProxyDetectDtos(updateDtos);
		logger.info("save ProxyDetectDto.insert:" + insertDtos.size() + ",update:" + updateDtos.size());
	}

	private void doAssort(List<ProxyDetectDto> dtoList, List<ProxyDetectDto> insertDtos, List<ProxyDetectDto> updateDtos) {
		Map<String, List<ProxyDetectDto>> domainDetectMap = new HashMap<String, List<ProxyDetectDto>>();
		for (ProxyDetectDto dto : dtoList) {
			List<ProxyDetectDto> domainList = domainDetectMap.get(dto.getDomain());
			if (domainList == null) {
				domainList = new ArrayList<ProxyDetectDto>();
				domainDetectMap.put(dto.getDomain(), domainList);
			}
			domainList.add(dto);

		}
		for (Entry<String, List<ProxyDetectDto>> entry : domainDetectMap.entrySet()) {
			Set<String> codeSet = new HashSet<String>();
			Map<String, ProxyDetectDto> dtoKeyMap = new HashMap<String, ProxyDetectDto>();
			for (ProxyDetectDto detectDto : entry.getValue()) {
				codeSet.add(detectDto.getAddrCode());
				dtoKeyMap.put(getDtoKey(detectDto), detectDto);
			}
			String domain = entry.getKey();
			List<String> addrCodeList = new ArrayList<String>(codeSet);
			List<ProxyDetectDto> hasDtos = getProxyDetectDtosByCodeList(addrCodeList, domain, null);
			Set<String> hasCodeSet = new HashSet<String>();
			for (ProxyDetectDto oldDto : hasDtos) {
				String key = getDtoKey(oldDto);
				hasCodeSet.add(key);
				ProxyDetectDto newDto = dtoKeyMap.get(key);
				if (newDto == null) {
					continue;
				}
				newDto.setMaxCost(newDto.getCurCost());
				newDto.setMinCost(newDto.getCurCost());
				if (oldDto.getMaxCost() != null && newDto.getMaxCost() < oldDto.getMaxCost()) {
					newDto.setMaxCost(oldDto.getMaxCost());
				}
				if (oldDto.getMinCost() != null && newDto.getMinCost() > oldDto.getMinCost()) {
					newDto.setMinCost(oldDto.getMinCost());
				}
				if (1 == newDto.getStatus()) {
					newDto.setStatus(ProxyDetectDto.STATUS_USABLE);
					newDto.setSuccessCount(oldDto.getSuccessCount() + 1);
					newDto.setLastSuccessCount(oldDto.getLastSuccessCount() + 1);
					newDto.setRetryTimes(0);
				} else if (0 == newDto.getStatus()) {
					newDto.setFailCount(oldDto.getFailCount() + 1);
					newDto.setLastSuccessCount(0);
					newDto.setRetryTimes(oldDto.getRetryTimes() + 1);
					if (newDto.getRetryTimes() >= ProxyDetectDto.MAX_RETRY_TIMES) {
						newDto.setRetryTimes(0);
						newDto.setStatus(oldDto.getStatus() - 1);
					}
				}
				newDto.setId(oldDto.getId());
				newDto.setCreateTime(oldDto.getCreateTime());
				updateDtos.add(newDto);
			}
			for (Entry<String, ProxyDetectDto> keyEntry : dtoKeyMap.entrySet()) {
				if (hasCodeSet.contains(keyEntry.getKey())) {
					continue;
				}
				ProxyDetectDto newDto = keyEntry.getValue();
				if (1 == newDto.getStatus()) {
					newDto.setSuccessCount(1);
					newDto.setLastSuccessCount(1);
				} else if (0 == newDto.getStatus()) {
					newDto.setFailCount(1);
				}
				newDto.setMaxCost(newDto.getCurCost());
				newDto.setMinCost(newDto.getCurCost());
				insertDtos.add(newDto);
			}
		}

	}

	private String getDtoKey(ProxyDetectDto dto) {
		String key = dto.getAddrCode() + "-" + dto.getDomain();
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

	@Override
	public List<ProxyDetectDto> getProxyDetectDtosFromStatus(Integer status, Date afterUpdateTime) {
		if (status == null) {
			return Collections.emptyList();
		}
		return proxyDetectDao.getProxyDetectDtosFromStatus(status, afterUpdateTime);
	}

	public void setProxyDetectDao(ProxyDetectDao proxyDetectDao) {
		this.proxyDetectDao = proxyDetectDao;
	}

	@Override
	public List<ProxyDetectDto> getProxyDetectDtoFromDomain(List<String> domainList, Integer status, Integer limit) {
		List<ProxyDetectDto> dtoList = new ArrayList<ProxyDetectDto>();
		BatchIterator<String> it = new BatchIterator<String>(domainList);
		while (it.hasNext()) {
			List<ProxyDetectDto> subList = proxyDetectDao.getProxyDetectDtoFromDomain(it.next(), status, limit);
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	@Override
	public void batchUpdateProxyStatus(List<Long> idList, Integer status) {
		BatchIterator<Long> it = new BatchIterator<Long>(idList);
		while (it.hasNext()) {
			proxyDetectDao.batchUpdateProxyStatus(it.next(), status);
		}
	}

	@Override
	public List<ProxyDetectDto> getProxyDetectDtosByCodeList(List<String> addrCodeList, String domain, Integer status) {
		if (CollectionUtils.isEmpty(addrCodeList)) {
			return Collections.emptyList();
		}
		return proxyDetectDao.getProxyDetectDtosByCodeList(addrCodeList, domain, status);
	}

}
