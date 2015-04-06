package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.yeam.tasker.cache.ProxyCacher;

public class ReloadProxyTimer {
	private static final int MAX_PROXY_LEN = 800;
	private static final int MIN_SUCCSS_COUNT = 10;
	private static Logger log = Logger.getLogger(ReloadProxyTimer.class);
	private static volatile boolean running = false;
	@Autowired
	private ProxyDetectService proxyDetectService;

	public void run() {
		if (running) {
			log.warn(this.getClass().getSimpleName() + " is working...");
			return;
		}
		try {
			running = true;
			long startMills = System.currentTimeMillis();
			List<String> domainList = getDomains();
			long cost = System.currentTimeMillis() - startMills;
			ProxyCacher proxyCacher = ProxyCacher.getInstance();
			int total = 0;
			int remain = 0;
			for (String domain : domainList) {
				List<ProxyDetectDto> dtoList = proxyDetectService.getProxyDetectDtoFromDomain(domainList, ProxyDetectDto.STATUS_USABLE, null);
				List<ProxyDetectDto> acceptList = doProxyFilters(dtoList);
				log.info("domain:" + domain + ",hasCount:" + dtoList.size() + ",accept:" + acceptList.size());
				total += dtoList.size();
				if (acceptList.isEmpty()) {
					continue;
				}
				remain += acceptList.size();
				Queue<ProxyDetectDto> domainQueue = new LinkedBlockingQueue<ProxyDetectDto>(acceptList);
				proxyCacher.addQueue(domain, domainQueue);
			}
			log.info("done.reload proxy.domain:" + domainList.size() + ",total:" + total + ",remain:" + remain + ",cost:" + cost + "ms");
		} finally {
			running = false;
		}

	}

	private List<ProxyDetectDto> doProxyFilters(List<ProxyDetectDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return dtoList;
		}
		List<ProxyDetectDto> tempList = new ArrayList<ProxyDetectDto>(dtoList.size());
		for (ProxyDetectDto dto : dtoList) {
			if (dto.getLastSuccessCount() >= MIN_SUCCSS_COUNT) {
				tempList.add(dto);
			}
		}
		Collections.sort(tempList, new Comparator<ProxyDetectDto>() {
			@Override
			public int compare(ProxyDetectDto o1, ProxyDetectDto o2) {
				int timeCmpValue = o2.getUpdateTime().compareTo(o1.getUpdateTime());
				// 更新时间倒序排
				if (timeCmpValue != 0) {
					return timeCmpValue;
				}
				// 一小时内跟新的，按成功数倒排
				int sCount = o2.getLastSuccessCount() - o1.getLastSuccessCount();
				if (sCount != 0) {
					return sCount;
				}
				// http > socket
				return o1.getType().compareTo(o2.getType());
			}
		});
		int toIndex = tempList.size() < MAX_PROXY_LEN ? tempList.size() : MAX_PROXY_LEN;
		return tempList.subList(0, toIndex);
	}

	private List<String> getDomains() {
		List<String> domainList = new ArrayList<String>();
		domainList.add("baidu.com");
		return domainList;
	}
}
