package com.lezo.iscript.yeam.server.session;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

public class ProxySessionCacher {
	private static final ProxySessionCacher INSTANCE = new ProxySessionCacher();
	private ConcurrentHashMap<String, IoSession> sessionMap = new ConcurrentHashMap<String, IoSession>();

	private ProxySessionCacher() {

	}

	public static ProxySessionCacher getInstance() {
		return INSTANCE;
	}

	public IoSession getSession(String key) {
		return sessionMap.get(key);
	}

	public void putIfAbsent(String key, IoSession session) {
		sessionMap.putIfAbsent(key, session);
	}

	public IoSession remove(String key) {
		return sessionMap.remove(key);
	}
}