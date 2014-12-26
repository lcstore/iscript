package com.lezo.iscript.service.crawler;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigClassUtils {
	private static final Object ADD_LOCKER = new Object();
	private static ConcurrentHashMap<String, Class<?>> nameClassMap = new ConcurrentHashMap<String, Class<?>>();

	public static Class<?> getDtoClass(String name) throws ClassNotFoundException {
		Class<?> destCls = nameClassMap.get(name);
		if (destCls != null) {
			return destCls;
		}
		synchronized (ADD_LOCKER) {
			destCls = nameClassMap.get(name);
			if (destCls != null) {
				return destCls;
			}
			String clsName = null;
			if (name.indexOf('.') < 0) {
				clsName = "com.lezo.iscript.service.crawler.dto." + name;
			}
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			try {
				destCls = classLoader.loadClass(clsName);
				nameClassMap.put(name, destCls);
			} catch (ClassNotFoundException e) {
				if (name.indexOf('.') < 0) {
					clsName = "com.lezo.iscript.yeam.resultmgr.vo." + name;
				}
				destCls = classLoader.loadClass(clsName);
				nameClassMap.put(name, destCls);
			}
		}
		return destCls;
	}
}
