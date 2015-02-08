package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class ProxyDetectWriter implements ObjectWriter<ProxyDetectDto> {
	private ProxyDetectService callService = SpringBeanUtils.getBean(ProxyDetectService.class);

	@Override
	public void write(List<ProxyDetectDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		saveDetectList(dataList);
	}

	private void saveDetectList(List<ProxyDetectDto> detectList) {
		if (detectList.isEmpty()) {
			return;
		}
		synchronized (ProxyDetectWriter.class) {
			callService.batchSaveProxyDetectDtos(detectList);
		}
	}

}
