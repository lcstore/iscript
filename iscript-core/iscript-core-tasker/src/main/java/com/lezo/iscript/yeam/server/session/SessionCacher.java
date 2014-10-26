package com.lezo.iscript.yeam.server.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.spi.LoggerFactory;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;

import com.lezo.iscript.service.crawler.dto.SessionHisDto;

public class SessionCacher {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(SessionCacher.class);
	private static final SessionCacher INSTANCE = new SessionCacher();
	private static final long SESSION_TIME_OUT = 5 * 60 * 1000;
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
		long currentTimeMillis = System.currentTimeMillis();
		while (it.hasNext()) {
			Entry<String, IoSession> entry = it.next();
			IoSession session = entry.getValue();
			if (!session.isConnected() || session.isClosing()) {
				closeList.add(entry.getValue());
				it.remove();
			} else if (currentTimeMillis - session.getLastIoTime() > SESSION_TIME_OUT) {
				session.close(false);
				closeList.add(session);
				it.remove();
			}
			logger.info("id:{},name:{},ioTime:{},close:{}", session.getAttribute(SessionHisDto.SESSION_ID), session.getAttribute(SessionHisDto.CLIEN_NAME), new Date(session.getLastIoTime()), session.getCloseFuture().isClosed());
		}
		return closeList;
	}
}