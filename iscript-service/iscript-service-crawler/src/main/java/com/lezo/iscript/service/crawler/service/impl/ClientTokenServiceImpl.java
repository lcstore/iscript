package com.lezo.iscript.service.crawler.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ClientTokenDao;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;
import com.lezo.iscript.service.crawler.service.ClientTokenService;
import com.lezo.iscript.utils.BatchIterator;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月6日
 */
@Service
public class ClientTokenServiceImpl implements ClientTokenService {
	@Autowired
	private ClientTokenDao clientTokenDao;

	@Override
	public void batchInsertDtos(List<ClientTokenDto> dtoList) {
		BatchIterator<ClientTokenDto> it = new BatchIterator<ClientTokenDto>(dtoList);
		while (it.hasNext()) {
			clientTokenDao.batchInsert(it.next());
		}

	}

	@Override
	public void batchUpdateDtos(List<ClientTokenDto> dtoList) {
		BatchIterator<ClientTokenDto> it = new BatchIterator<ClientTokenDto>(dtoList);
		while (it.hasNext()) {
			clientTokenDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<ClientTokenDto> dtoList) {
		batchInsertDtos(dtoList);
	}

	@Override
	public List<ClientTokenDto> getClientTokenDtoByRefreshDate(Date fromRefreshDate, Date toRefreshDate, String clientType) {
		return clientTokenDao.getClientTokenDtoByRefreshDate(fromRefreshDate, toRefreshDate, clientType);
	}

	@Override
	public List<ClientTokenDto> getClientTokenDtoByUpdateTime(Date afterTime) {
		return clientTokenDao.getClientTokenDtoByUpdateTime(afterTime);
	}

}
