package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;
import com.lezo.iscript.service.crawler.service.ProxyAddrService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class ProxyCollectWriter implements ObjectWriter<ProxyAddrDto> {
	private static Logger logger = Logger.getLogger(ProxyCollectWriter.class);
	private ProxyAddrService proxyAddrService = SpringBeanUtils.getBean(ProxyAddrService.class);

	@Override
	public synchronized void write(List<ProxyAddrDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		proxyAddrService.batchSaveProxyAddrs(dataList);
		logger.info("save data size:" + dataList.size());
	}

}
