package com.lezo.iscript.yeam.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.service.crawler.service.SessionHisService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.server.event.RequestProceser;
import com.lezo.iscript.yeam.server.event.RequestWorker;
import com.lezo.iscript.yeam.server.session.SessionCacher;

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
		// 读写 通道均在600 秒内无任何操作就进入空闲状态
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 600);
		acceptor.bind(new InetSocketAddress(port));

		resetSessions();
		resetProxys();
		logger.info("start to listener port:" + port + " for IoServer..");
	}

	private void resetSessions() {
		SessionHisService sessionHisService = SpringBeanUtils.getBean(SessionHisService.class);
		sessionHisService.updateUpSessionToInterrupt();
	}

	private void resetProxys() {
		ProxyDetectService proxyDetectService = SpringBeanUtils.getBean(ProxyDetectService.class);
		List<Long> idList = new ArrayList<Long>();
		List<ProxyDetectDto> workList = proxyDetectService.getProxyDetectDtosFromId(0L, Integer.MAX_VALUE, ProxyDetectDto.STATUS_WORK);
		for (ProxyDetectDto dto : workList) {
			idList.add(dto.getId());
		}
		proxyDetectService.batchUpdateProxyStatus(idList, ProxyDetectDto.STATUS_RETRY);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		add2Attribute(session, SessionHisDto.REQUEST_SIZE, 1);
		addNewSession(session, message);
		RequestProceser.getInstance().execute(new RequestWorker(session, message));
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		add2Attribute(session, SessionHisDto.RESPONE_SIZE, 1);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		resetSession(session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		SessionHisDto downDto = getSessionHisDto(session);
		if (!StringUtils.isEmpty(downDto.getClienName())) {
			logger.warn(String.format("close: %s", downDto));
		} else {
			logger.warn(String.format("close client,not found name for sessionId:%s", downDto.getSessionId()));
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		add2Attribute(session, SessionHisDto.ERROR_SIZE, 1);
		SessionHisDto trackDto = getSessionHisDto(session);
		logger.warn(String.format("%s,remote:%s,local:%s,cause:", trackDto, session.getRemoteAddress(), session.getLocalAddress()), cause);
	}

	public void add2Attribute(IoSession session, String key, int num) {
		int newValue = (Integer) session.getAttribute(key) + num;
		session.setAttribute(key, newValue);
	}

	private void addNewSession(IoSession session, Object message) {
		if (message instanceof IoRequest && !session.containsAttribute(SessionHisDto.CLIEN_NAME)) {
			IoRequest ioRequest = (IoRequest) message;
			JSONObject hObject = JSONUtils.getJSONObject(ioRequest.getHeader());
			String name = JSONUtils.getString(hObject, SessionHisDto.CLIEN_NAME);
			if (!StringUtils.isEmpty(name)) {
				logger.info(String.format("add new client:%s", name));
				session.setAttribute(SessionHisDto.CLIEN_NAME, name);
				SessionCacher.getInstance().putIfAbsent(name, session);
			} else {
				logger.warn(String.format("can not found name from %s", hObject));
			}
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
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		if (session.isBothIdle()) {
			session.close(true);
			SessionHisDto sessionDto = getSessionHisDto(session);
			logger.info(String.format("Close idle session:%s", sessionDto));
		}
	}

}
