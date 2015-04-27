package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;

public interface ProxyAddrDao {
	void batchInsert(List<ProxyAddrDto> dtoList);

	void batchUpdate(List<ProxyAddrDto> dtoList);

	List<ProxyAddrDto> getProxyAddrDtosByAddrCodes(@Param("addrCodeList") List<String> addrCodeList);

	List<ProxyAddrDto> getNullRegionProxyAddrDtos(@Param("fromId") Long fromId, @Param("type") Integer type, @Param("limit") Integer limit);

	List<ProxyAddrDto> getProxyAddrDtosByCreateTime(@Param("afterTime") Date afterTime);

	List<ProxyAddrDto> getProxyAddrDtosByFromId(@Param("fromId") Long fromId, @Param("limit") Integer limit);

	void batchDeleteByIds(@Param("idList") List<String> idList);

	void batchUpdateProxyDetectByCodeList(@Param("codeList") List<String> codeList,@Param("usable")  int usable);

	void batchUpdateProxyRegionById(List<ProxyAddrDto> dtoList);
}
