package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProxyCollectHisDto;
import com.lezo.iscript.service.crawler.service.ProxyCollectHisService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class ProxyCollectHisWriter implements ObjectWriter<ProxyCollectHisDto> {
	private static Logger logger = Logger.getLogger(ProxyCollectHisWriter.class);
	private ProxyCollectHisService proxyCollectHisService = SpringBeanUtils.getBean(ProxyCollectHisService.class);

	@Override
	public void write(List<ProxyCollectHisDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		proxyCollectHisService.batchSaveDtos(dataList);
		logger.info("save data size:" + dataList.size());
	}

}
