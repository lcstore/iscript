package com.lezo.iscript.yeam.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.yeam.server.event.RequestProceser;
import com.lezo.iscript.yeam.server.event.RequestWorker;

public class IoServer extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(IoServer.class);
	private IoAcceptor acceptor;

	public IoServer(int port) throws IOException {
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		if (logger.isDebugEnabled()) {
			acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		}
		acceptor.setHandler(this);
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(port));
		logger.info("start to listener port:" + port + " for IoServer..");
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message == null) {
			return;
		}
		RequestProceser.getInstance().execute(new RequestWorker(session, message));
		addTrackSession(session);
	}

	private void addTrackSession(IoSession session) {
		String key = SessionHisDto.SAVE_STAMP;
		Long stamp = (Long) session.getAttribute(key);
		long cost = System.currentTimeMillis() - stamp;
		if (cost >= SessionHisDto.MAX_SAVE_INTERVAL) {
			StorageBuffer<SessionHisDto> storage = StorageBufferFactory.getStorageBuffer(SessionHisDto.class);
			SessionHisDto trackDto = getSessionHisDto(session);
			trackDto.setStatus(SessionHisDto.STATUS_UP);
			storage.add(trackDto);
			session.setAttribute(key, System.currentTimeMillis());
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		String key = SessionHisDto.RESPONE_SIZE;
		int newValue = (Integer) session.getAttribute(key) + 1;
		session.setAttribute(key, newValue);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		List<SessionHisDto> dtoList = new ArrayList<SessionHisDto>(2);
		addLostSession(session, dtoList);
		resetSession(session);
		addNewSession(session, dtoList);
		StorageBuffer<SessionHisDto> storage = StorageBufferFactory.getStorageBuffer(SessionHisDto.class);
		storage.addAll(dtoList);
	}

	private void addNewSession(IoSession session, List<SessionHisDto> dtoList) {
		// new session
		SessionHisDto newDto = getSessionHisDto(session);
		dtoList.add(newDto);
	}

	private void addLostSession(IoSession session, List<SessionHisDto> dtoList) {
		Long loseTime = (Long) session.getAttribute(SessionHisDto.LOSE_TIME);
		if (loseTime != null) {
			// save the last lost session
			SessionHisDto lostDto = getSessionHisDto(session);
			lostDto.setUpdateTime(new Date(loseTime));
			lostDto.setStatus(SessionHisDto.STATUS_DOWN);
			dtoList.add(lostDto);
			session.removeAttribute(SessionHisDto.LOSE_TIME);
		}
	}

	private SessionHisDto getSessionHisDto(IoSession session) {
		SessionHisDto dto = new SessionHisDto();
		String clientName = (String) session.getAttribute(SessionHisDto.CLIEN_NAME);
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		dto.setSessionId((String) session.getAttribute(SessionHisDto.SESSION_ID));
		dto.setClienName(clientName);
		dto.setRequestSize((Integer) session.getAttribute(SessionHisDto.REQUEST_SIZE));
		dto.setResponeSize((Integer) session.getAttribute(SessionHisDto.RESPONE_SIZE));
		dto.setErrorSize((Integer) session.getAttribute(SessionHisDto.ERROR_SIZE));
		dto.setSuccessNum((Integer) session.getAttribute(SessionHisDto.SUCCESS_NUM));
		dto.setFailNum((Integer) session.getAttribute(SessionHisDto.FAIL_NUM));
		return dto;
	}

	private void resetSession(IoSession session) {
		String newSessionId = UUID.randomUUID().toString();
		session.setAttribute(SessionHisDto.SESSION_ID, newSessionId);
		session.setAttribute(SessionHisDto.REQUEST_SIZE, 0);
		session.setAttribute(SessionHisDto.RESPONE_SIZE, 0);
		session.setAttribute(SessionHisDto.ERROR_SIZE, 0);
		session.setAttribute(SessionHisDto.SUCCESS_NUM, 0);
		session.setAttribute(SessionHisDto.FAIL_NUM, 0);
		session.setAttribute(SessionHisDto.SAVE_STAMP, System.currentTimeMillis());
		session.removeAttribute(SessionHisDto.LOSE_TIME);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		SessionHisDto downDto = getSessionHisDto(session);
		downDto.setStatus(SessionHisDto.STATUS_DOWN);
		StorageBuffer<SessionHisDto> storage = StorageBufferFactory.getStorageBuffer(SessionHisDto.class);
		storage.add(downDto);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		String key = SessionHisDto.ERROR_SIZE;
		int newValue = (Integer) session.getAttribute(key) + 1;
		session.setAttribute(key, newValue);
	}

}
