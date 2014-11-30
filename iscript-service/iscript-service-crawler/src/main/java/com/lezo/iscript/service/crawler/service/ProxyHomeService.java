package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.ProxyHomeDto;

public interface ProxyHomeService extends BaseService<ProxyHomeDto> {
	List<ProxyHomeDto> getProxyHomeDtoByStatus(Integer status);

	void batchUpdateProxyHomeDtoStatus(List<Long> idList, int status);

	List<ProxyHomeDto> getProxyHomeDtoByUrl(List<String> urlList);
}
