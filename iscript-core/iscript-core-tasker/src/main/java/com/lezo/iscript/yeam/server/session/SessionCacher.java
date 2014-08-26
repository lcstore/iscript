package com.lezo.iscript.yeam.server.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

public class SessionCacher {
	private static final SessionCacher INSTANCE = new SessionCacher();
	private ConcurrentHashMap<String, IoSession> sessionMap = new ConcurrentHashMap<String, IoSession>();

	private SessionCacher() {

	}

	public static SessionCacher getInstance() {
		return INSTANCE;
	}

	public IoSession getSession(String key) {
		return sessionMap.get(key);
	}

	public void putIfAbsent(String key, IoSession session) {
		sessionMap.putIfAbsent(key, session);
	}

	public Set<Entry<String, IoSession>> entrySet() {
		return sessionMap.entrySet();
	}

	public List<IoSession> removeClosed() {
		List<IoSession> closeList = new ArrayList<IoSession>();
		Iterator<Entry<String, IoSession>> it = sessionMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, IoSession> entry = it.next();
			if (!entry.getValue().isConnected()) {
				closeList.add(entry.getValue());
				it.remove();
			}
		}
		return closeList;
	}
}
