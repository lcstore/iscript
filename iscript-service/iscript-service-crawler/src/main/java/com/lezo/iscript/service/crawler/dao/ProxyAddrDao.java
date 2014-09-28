package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;

public interface ProxyAddrDao {
	void batchInsert(List<ProxyAddrDto> dtoList);

	void batchUpdate(List<ProxyAddrDto> dtoList);

	ProxyAddrDto getProxyAddrDto(@Param(value = "ip") Long ip, @Param(value = "port") int port);

	List<ProxyAddrDto> getProxyAddrDtos(List<Integer> ipList);
}
