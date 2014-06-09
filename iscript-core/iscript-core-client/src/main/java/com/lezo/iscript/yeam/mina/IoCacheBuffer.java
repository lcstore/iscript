package com.lezo.iscript.yeam.mina;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class IoCacheBuffer {
	private static final ConcurrentHashMap<String, IoCache> ioCacheMap = new ConcurrentHashMap<String, IoCache>();

	public static IoCache getIoCache(InetSocketAddress socketAddress) {
		String key = socketAddress.toString();
		IoCache ioCache = ioCacheMap.get(key);
		return ioCache;
	}

	public static void addIoCache(IoCache ioCache) {
		String key = ioCache.getSocketAddress().toString();
		ioCacheMap.putIfAbsent(key, ioCache);
	}
}
