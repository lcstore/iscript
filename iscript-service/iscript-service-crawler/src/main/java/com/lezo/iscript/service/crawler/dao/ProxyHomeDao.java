package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.ProxyHomeDto;

public interface ProxyHomeDao extends BaseDao<ProxyHomeDto> {

	List<ProxyHomeDto> getProxyHomeDtoByStatus(@Param("status") Integer status, @Param("isDelete") Integer isDelete);

	void batchUpdateProxyHomeDtoStatus(@Param("idList") List<Long> idList, @Param("status") int status);

	List<ProxyHomeDto> getProxyHomeDtoByUrl(@Param("urlList") List<String> urlList, @Param("isDelete") Integer isDelete);
}
