package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.ProxyHomeDto;

public interface ProxyHomeDao extends BaseDao<ProxyHomeDto> {

	List<ProxyHomeDto> getProxyHomeDtoByStatus(@Param(value = "status") Integer status);

	void batchUpdateProxyHomeDtoStatus(@Param(value = "idList") List<Long> idList, @Param(value = "status") int status);

	List<ProxyHomeDto> getProxyHomeDtoByUrl(@Param("urlList") List<String> urlList);
}
