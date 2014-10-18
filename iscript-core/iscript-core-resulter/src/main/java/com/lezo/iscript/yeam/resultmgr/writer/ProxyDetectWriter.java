package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
		List<ProxyDetectDto> detectList = new ArrayList<ProxyDetectDto>();
		List<ProxyDetectDto> collectList = new ArrayList<ProxyDetectDto>();
		doAssort(dataList, detectList, collectList);

		saveDetectList(detectList);
		saveCollectList(collectList);
	}

	private void saveCollectList(List<ProxyDetectDto> collectList) {
		if (collectList.isEmpty()) {
			return;
		}
		synchronized (this) {
			callService.batchInsertIfAbsent(collectList);
		}
	}

	private void saveDetectList(List<ProxyDetectDto> detectList) {
		if (detectList.isEmpty()) {
			return;
		}
		synchronized (this) {
			callService.batchSaveAfterDetect(detectList);
		}
	}

	private void doAssort(List<ProxyDetectDto> dataList, List<ProxyDetectDto> detectList, List<ProxyDetectDto> collectList) {
		for (ProxyDetectDto dto : dataList) {
			if (StringUtils.isEmpty(dto.getUrl())) {
				collectList.add(dto);
			} else {
				detectList.add(dto);
			}
		}

	}

	@Override
	public void flush() {

	}

}
