package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;

public interface ProxyDetectDao {
	void batchInsert(List<ProxyDetectDto> dtoList);

	void batchUpdate(List<ProxyDetectDto> dtoList);

	List<ProxyDetectDto> getProxyDetectDtos(@Param(value = "ipLongs") List<Long> ipLongs,
			@Param(value = "portList") List<Integer> portList, @Param(value = "status") Integer status);

	List<ProxyDetectDto> getProxyDetectDtosFromId(@Param(value = "fromId") Long fromId,
			@Param(value = "limit") int limit, @Param(value = "status") Integer status);

	List<ProxyDetectDto> getProxyDetectDtosFromStatus(@Param(value = "status") Integer status,
			@Param(value = "afterUpdateTime") Date afterUpdateTime);

	List<ProxyDetectDto> getProxyDetectDtoFromDomain(@Param(value = "domainList") List<String> domainList,
			@Param(value = "status") Integer status);

	List<ProxyDetectDto> getUnionProxyDetectDtoFromDomain(@Param(value = "domainList") List<String> domainList,
			@Param(value = "status") Integer status, @Param(value = "limit") int limit);

	void batchUpdateProxyStatus(@Param(value = "idList") List<Long> idList, @Param(value = "status") Integer status);

	List<ProxyDetectDto> getProxyDetectDtosByCodeList(@Param("addrCodeList") List<String> addrCodeList, @Param("domain") String domain, @Param("status") Integer status);
}
