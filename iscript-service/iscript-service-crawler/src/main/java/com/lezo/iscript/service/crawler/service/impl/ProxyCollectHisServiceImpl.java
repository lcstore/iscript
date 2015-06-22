package com.lezo.iscript.service.crawler.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.common.UnifyValueUtils;
import com.lezo.iscript.service.crawler.dao.ProxyCollectHisDao;
import com.lezo.iscript.service.crawler.dto.ProxyCollectHisDto;
import com.lezo.iscript.service.crawler.service.ProxyCollectHisService;
import com.lezo.iscript.utils.BatchIterator;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年2月5日
 */
@Service
public class ProxyCollectHisServiceImpl implements ProxyCollectHisService {
	@Autowired
	private ProxyCollectHisDao proxyCollectHisDao;

	@Override
	public int batchInsertDtos(List<ProxyCollectHisDto> dtoList) {
		int affect = 0;
		UnifyValueUtils.unifyQuietly(dtoList);
		BatchIterator<ProxyCollectHisDto> it = new BatchIterator<ProxyCollectHisDto>(dtoList);
		while (it.hasNext()) {
			affect += proxyCollectHisDao.batchInsert(it.next());
		}
		return affect;
	}

	@Override
	public int batchUpdateDtos(List<ProxyCollectHisDto> dtoList) {
		int affect = 0;
		BatchIterator<ProxyCollectHisDto> it = new BatchIterator<ProxyCollectHisDto>(dtoList);
		while (it.hasNext()) {
			affect += proxyCollectHisDao.batchUpdate(it.next());
		}
		return affect;
	}

	@Override
	public int batchSaveDtos(List<ProxyCollectHisDto> dtoList) {
		return batchInsertDtos(dtoList);
	}

	@Override
	public List<ProxyCollectHisDto> getProxyCollectHisDtoByFromId(Long fromId, int limit) {
		if (limit <= 0) {
			return Collections.emptyList();
		}
		return proxyCollectHisDao.getProxyCollectHisDtoByFromId(fromId, limit);
	}

	public void setProxyCollectHisDao(ProxyCollectHisDao proxyCollectHisDao) {
		this.proxyCollectHisDao = proxyCollectHisDao;
	}

}
