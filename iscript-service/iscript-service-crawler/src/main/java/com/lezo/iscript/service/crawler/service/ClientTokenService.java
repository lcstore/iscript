package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;

public interface ClientTokenService extends BaseService<ClientTokenDto> {
	List<ClientTokenDto> getClientTokenDtoByRefreshDate(Date fromRefreshDate, Date toRefreshDate, String clientType);

	List<ClientTokenDto> getClientTokenDtoByUpdateTime(Date afterTime);
}
