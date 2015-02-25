package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;

public interface ProxyAddrService {
	void batchInsertProxyAddrs(List<ProxyAddrDto> dtoList);

	void batchUpdateProxyAddrs(List<ProxyAddrDto> dtoList);

	void batchSaveProxyAddrs(List<ProxyAddrDto> dtoList);

	List<ProxyAddrDto> getProxyAddrDtosByAddrCodes(List<String> addrCodeList);

	List<ProxyAddrDto> getNullRegionProxyAddrDtos(Long fromId, Integer type, Integer limit);

	List<ProxyAddrDto> getProxyAddrDtosByCreateTime(Date afterTime);
}
