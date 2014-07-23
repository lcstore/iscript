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
		addNewSession(session, dtoList);
		StorageBuffer<SessionHisDto> storage = StorageBufferFactory.getStorageBuffer(SessionHisDto.class);
		storage.addAll(dtoList);
	}

	private void addNewSession(IoSession session, List<SessionHisDto> dtoList) {
		// new session
		String clientName = (String) session.getAttribute(SessionHisDto.CLIEN_NAME);
		String newSessionId = UUID.randomUUID().toString();
		SessionHisDto newDto = new SessionHisDto();
		newDto.setSessionId(newSessionId);
		newDto.setCreateTime(new Date());
		newDto.setUpdateTime(newDto.getCreateTime());
		newDto.setClienName(clientName);
		newDto.setRequestSize(0);
		newDto.setResponeSize(0);
		newDto.setErrorSize(0);
		newDto.setSuccessNum(0);
		newDto.setFailNum(0);
		dtoList.add(newDto);
		resetSession(session, newSessionId);
	}

	private void addLostSession(IoSession session, List<SessionHisDto> dtoList) {
		Long loseTime = (Long) session.getAttribute(SessionHisDto.LOSE_TIME);
		if (loseTime != null) {
			// save the last lost session
			String clientName = (String) session.getAttribute(SessionHisDto.CLIEN_NAME);
			SessionHisDto lostDto = new SessionHisDto();
			lostDto.setUpdateTime(new Date(loseTime));
			lostDto.setSessionId((String) session.getAttribute(SessionHisDto.SESSION_ID));
			lostDto.setClienName(clientName);
			lostDto.setRequestSize((Integer) session.getAttribute(SessionHisDto.REQUEST_SIZE));
			lostDto.setResponeSize((Integer) session.getAttribute(SessionHisDto.RESPONE_SIZE));
			lostDto.setErrorSize((Integer) session.getAttribute(SessionHisDto.ERROR_SIZE));
			lostDto.setSuccessNum((Integer) session.getAttribute(SessionHisDto.SUCCESS_NUM));
			lostDto.setFailNum((Integer) session.getAttribute(SessionHisDto.FAIL_NUM));
			lostDto.setStatus(SessionHisDto.STATUS_DOWN);
			dtoList.add(lostDto);

			session.removeAttribute(SessionHisDto.LOSE_TIME);
		}
	}

	private void resetSession(IoSession session, String newSessionId) {
		session.setAttribute(SessionHisDto.SESSION_ID, newSessionId);
		session.setAttribute(SessionHisDto.REQUEST_SIZE, 0);
		session.setAttribute(SessionHisDto.RESPONE_SIZE, 0);
		session.setAttribute(SessionHisDto.ERROR_SIZE, 0);
		session.setAttribute(SessionHisDto.SUCCESS_NUM, 0);
		session.setAttribute(SessionHisDto.FAIL_NUM, 0);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		String clientName = (String) session.getAttribute(SessionHisDto.CLIEN_NAME);
		SessionHisDto downDto = new SessionHisDto();
		downDto.setUpdateTime(new Date());
		downDto.setSessionId((String) session.getAttribute(SessionHisDto.SESSION_ID));
		downDto.setClienName(clientName);
		downDto.setRequestSize((Integer) session.getAttribute(SessionHisDto.REQUEST_SIZE));
		downDto.setResponeSize((Integer) session.getAttribute(SessionHisDto.RESPONE_SIZE));
		downDto.setErrorSize((Integer) session.getAttribute(SessionHisDto.ERROR_SIZE));
		downDto.setSuccessNum((Integer) session.getAttribute(SessionHisDto.SUCCESS_NUM));
		downDto.setFailNum((Integer) session.getAttribute(SessionHisDto.FAIL_NUM));
		downDto.setStatus(SessionHisDto.STATUS_DOWN);

		StorageBuffer<SessionHisDto> storage = StorageBufferFactory.getStorageBuffer(SessionHisDto.class);
		storage.add(downDto);
	}

}
