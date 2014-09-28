package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;

public interface ProxyAddrService {
	void batchInsertProxyAddrs(List<ProxyAddrDto> dtoList);

	void batchUpdateProxyAddrs(List<ProxyAddrDto> dtoList);

	ProxyAddrDto getProxyAddrDto(String ipString, int port);
	
	ProxyAddrDto getProxyAddrDto(Long ipValue, int port);
}
