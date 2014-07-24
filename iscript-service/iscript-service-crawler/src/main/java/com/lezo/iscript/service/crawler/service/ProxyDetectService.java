package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;

public interface ProxyDetectService {
	void batchInsertProxyDetectDtos(List<ProxyDetectDto> dtoList);

	void batchUpdateProxyDetectDtos(List<ProxyDetectDto> dtoList);

	void batchInsertIfAbsent(List<ProxyDetectDto> dtoList);

	void batchSaveAfterDetect(List<ProxyDetectDto> dtoList);

	List<ProxyDetectDto> getProxyDetectDtos(List<Long> ipLongs, List<Integer> portList, Integer status);

	List<ProxyDetectDto> getProxyDetectDtosFromId(Long fromId, int limit, Integer status);

	List<ProxyDetectDto> getProxyDetectDtosFromStatus(Integer status, Date afterUpdateTime);

	List<ProxyDetectDto> getProxyDetectDtoFromDomain(List<String> domainList, Integer status);
}
