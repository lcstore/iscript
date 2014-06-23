package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;

public interface ProxyAddrDao {
	void batchInsert(List<ProxyAddrDto> dtoList);
	void batchUpdate(List<ProxyAddrDto> dtoList);
	ProxyAddrDto getProxyAddrDto(int ipValue,int port);
}
