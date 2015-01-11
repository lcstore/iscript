package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;

public interface ProxyAddrDao {
	void batchInsert(List<ProxyAddrDto> dtoList);

	void batchUpdate(List<ProxyAddrDto> dtoList);

	List<ProxyAddrDto> getProxyAddrDtosByAddrCodes(@Param("addrCodeList") List<String> addrCodeList);

	List<ProxyAddrDto> getNullRegionProxyAddrDtos(@Param("type") Integer type);
}
