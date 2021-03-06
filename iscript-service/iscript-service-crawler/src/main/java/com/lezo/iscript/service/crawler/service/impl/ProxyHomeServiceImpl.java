package com.lezo.iscript.service.crawler.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProxyHomeDao;
import com.lezo.iscript.service.crawler.dto.ProxyHomeDto;
import com.lezo.iscript.service.crawler.service.ProxyHomeService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ProxyHomeServiceImpl implements ProxyHomeService {
	@Autowired
	private ProxyHomeDao proxyHomeDao;

	@Override
	public int batchInsertDtos(List<ProxyHomeDto> dtoList) {
		int affect = 0;
		BatchIterator<ProxyHomeDto> it = new BatchIterator<ProxyHomeDto>(dtoList);
		while (it.hasNext()) {
			affect += proxyHomeDao.batchInsert(it.next());
		}
		return affect;
	}

	@Override
	public int batchUpdateDtos(List<ProxyHomeDto> dtoList) {
		int affect = 0;
		BatchIterator<ProxyHomeDto> it = new BatchIterator<ProxyHomeDto>(dtoList);
		while (it.hasNext()) {
			affect += proxyHomeDao.batchUpdate(it.next());
		}
		return affect;
	}

	@Override
	public int batchSaveDtos(List<ProxyHomeDto> dtoList) {
		return batchInsertDtos(dtoList);
	}

	@Override
	public List<ProxyHomeDto> getProxyHomeDtoByStatus(Integer status, Integer isDelete) {
		return proxyHomeDao.getProxyHomeDtoByStatus(status, isDelete);
	}

	@Override
	public void batchUpdateProxyHomeDtoStatus(List<Long> idList, int status) {
		if (CollectionUtils.isEmpty(idList)) {
			return;
		}
		proxyHomeDao.batchUpdateProxyHomeDtoStatus(idList, status);
	}

	@Override
	public List<ProxyHomeDto> getProxyHomeDtoByUrl(List<String> urlList, Integer isDelete) {
		if (CollectionUtils.isEmpty(urlList)) {
			return Collections.emptyList();
		}
		return proxyHomeDao.getProxyHomeDtoByUrl(urlList, isDelete);
	}

	public void setProxyHomeDao(ProxyHomeDao proxyHomeDao) {
		this.proxyHomeDao = proxyHomeDao;
	}

}
