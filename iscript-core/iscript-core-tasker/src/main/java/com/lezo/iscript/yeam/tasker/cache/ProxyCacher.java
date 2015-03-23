package com.lezo.iscript.yeam.tasker.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;

public class ProxyCacher {
	private static final ProxyCacher INSTANCE = new ProxyCacher();
	private ConcurrentHashMap<String, Queue<ProxyDetectDto>> proxyQueueMap = new ConcurrentHashMap<String, Queue<ProxyDetectDto>>();

	private ProxyCacher() {
	}

	public static ProxyCacher getInstance() {
		return INSTANCE;
	}

	public Queue<ProxyDetectDto> addQueue(String domain, Queue<ProxyDetectDto> queue) {
		return proxyQueueMap.put(domain, queue);
	}

	public Queue<ProxyDetectDto> getQueue(String domain) {
		return getOrSecond(domain, null);
	}

	public Queue<ProxyDetectDto> getOrSecond(String domain, String second) {
		Queue<ProxyDetectDto> domainQueue = proxyQueueMap.get(domain);
		if (domainQueue == null && second != null) {
			domainQueue = proxyQueueMap.get(second);
		}
		return domainQueue;
	}

	public int size() {
		int total = 0;
		for (Entry<String, Queue<ProxyDetectDto>> entry : proxyQueueMap.entrySet()) {
			total += entry.getValue().size();
		}
		return total;
	}

	public List<String> getKeys() {
		return new ArrayList<String>(proxyQueueMap.keySet());
	}
}
