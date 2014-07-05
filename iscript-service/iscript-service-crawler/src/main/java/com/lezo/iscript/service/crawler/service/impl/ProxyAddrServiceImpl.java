package com.lezo.iscript.service.crawler.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProxyAddrDao;
import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;
import com.lezo.iscript.service.crawler.service.ProxyAddrService;
import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.InetAddressUtils;

@Service
public class ProxyAddrServiceImpl implements ProxyAddrService {
	@Autowired
	private ProxyAddrDao proxyAddrDao;

	@Override
	public void batchInsertProxyAddrs(List<ProxyAddrDto> dtoList) {
		BatchIterator<ProxyAddrDto> it = new BatchIterator<ProxyAddrDto>(dtoList);
		while (it.hasNext()) {
			proxyAddrDao.batchInsert(it.next());
		}

	}

	@Override
	public void batchUpdateProxyAddrs(List<ProxyAddrDto> dtoList) {
		BatchIterator<ProxyAddrDto> it = new BatchIterator<ProxyAddrDto>(dtoList);
		while (it.hasNext()) {
			proxyAddrDao.batchUpdate(it.next());
		}
	}

	@Override
	public ProxyAddrDto getProxyAddrDto(String ipString, int port) {
		if (StringUtils.isEmpty(ipString) || port < 1) {
			return null;
		}
		return getProxyAddrDto(InetAddressUtils.inet_aton(ipString), port);
	}

	@Override
	public ProxyAddrDto getProxyAddrDto(Long ipValue, int port) {
		return proxyAddrDao.getProxyAddrDto(ipValue, port);
	}

	public void setProxyAddrDao(ProxyAddrDao proxyAddrDao) {
		this.proxyAddrDao = proxyAddrDao;
	}

}
