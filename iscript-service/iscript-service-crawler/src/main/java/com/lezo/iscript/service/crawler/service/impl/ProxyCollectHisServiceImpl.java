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
	public void batchInsertDtos(List<ProxyCollectHisDto> dtoList) {
		UnifyValueUtils.unifyQuietly(dtoList);
		BatchIterator<ProxyCollectHisDto> it = new BatchIterator<ProxyCollectHisDto>(dtoList);
		while (it.hasNext()) {
			proxyCollectHisDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateDtos(List<ProxyCollectHisDto> dtoList) {
		BatchIterator<ProxyCollectHisDto> it = new BatchIterator<ProxyCollectHisDto>(dtoList);
		while (it.hasNext()) {
			proxyCollectHisDao.batchUpdate(it.next());
		}
	}

	@Override
	public void batchSaveDtos(List<ProxyCollectHisDto> dtoList) {
		batchInsertDtos(dtoList);
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
