package com.lezo.iscript.service.crawler.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.ProxyCollectHisDto;

public interface ProxyCollectHisService extends BaseService<ProxyCollectHisDto> {
	List<ProxyCollectHisDto> getProxyCollectHisDtoByFromId(@Param("fromId") Long fromId, @Param("limit") int limit);
}
