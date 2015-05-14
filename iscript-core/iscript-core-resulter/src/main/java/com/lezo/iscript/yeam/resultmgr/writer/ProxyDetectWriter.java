package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.service.ProxyAddrService;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class ProxyDetectWriter implements ObjectWriter<ProxyDetectDto> {
	private static Logger logger = LoggerFactory.getLogger(ProxyDetectWriter.class);
	private ProxyDetectService callService = SpringBeanUtils.getBean(ProxyDetectService.class);
	private ProxyAddrService proxyAddrService = SpringBeanUtils.getBean(ProxyAddrService.class);

	@Override
	public void write(List<ProxyDetectDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		synchronized (ProxyDetectWriter.class) {
			callService.batchSaveProxyDetectDtos(dataList);
		}
		//use a job to summary
//		doDetectSummary(dataList);
	}

	private void doDetectSummary(List<ProxyDetectDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		Map<String, Set<String>> keyCodeMap = new HashMap<String, Set<String>>();
		for (ProxyDetectDto dto : dtoList) {
			String key = dto.getStatus() + ":" + dto.getDomain();
			Set<String> codeSet = keyCodeMap.get(key);
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				keyCodeMap.put(key, codeSet);
			}
			codeSet.add(dto.getAddrCode());
		}
		for (Entry<String, Set<String>> entry : keyCodeMap.entrySet()) {
			String key = entry.getKey();
			Integer status = Integer.valueOf(key.split(":")[0]);
			proxyAddrService.batchUpdateProxyDetectByCodeList(new ArrayList<String>(entry.getValue()), status);
			logger.info("detect summary,status:" + key + ",code size:" + entry.getValue().size() + ",total:"
					+ dtoList.size());
		}

	}

}
