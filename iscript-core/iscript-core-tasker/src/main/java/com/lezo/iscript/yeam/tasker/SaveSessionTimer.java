package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.service.crawler.service.SessionHisService;
import com.lezo.iscript.yeam.server.IoAcceptorHolder;

public class SaveSessionTimer {
	private static Logger logger = Logger.getLogger(SaveSessionTimer.class);
	private static volatile boolean running = false;
	@Autowired
	private SessionHisService sessionHisService;

	public void run() {
		if (running) {
			logger.warn("SaveSessionTimer is working...");
			return;
		}
		try {
			running = true;
			long start = System.currentTimeMillis();
			List<SessionHisDto> copyList = new ArrayList<SessionHisDto>();
			IoAcceptor ioAcceptor = IoAcceptorHolder.getIoAcceptor();
			Map<Long, IoSession> sessionMap = ioAcceptor.getManagedSessions();
			int closeCount = 0;
			for (Entry<Long, IoSession> entry : sessionMap.entrySet()) {
				IoSession session = entry.getValue();
				SessionHisDto sessionHisDto = getSessionHisDto(session);
				if (session.getCloseFuture().isClosed()) {
					sessionHisDto.setStatus(SessionHisDto.STATUS_DOWN);
					closeCount++;
				}
				if (StringUtils.isEmpty(sessionHisDto.getClienName())) {
					logger.warn("empty name.id:" + sessionHisDto.getSessionId() + "," + sessionHisDto);
					continue;
				}
				copyList.add(sessionHisDto);
			}
			sessionHisService.updateSessionByStatus(SessionHisDto.STATUS_UP, SessionHisDto.STATUS_DOWN);
			sessionHisService.batchSaveSessionHisDtos(copyList);
			long cost = System.currentTimeMillis() - start;
			logger.info(String.format("Save SessionHisDto,close:%d,active:%d,cost:%s", closeCount, copyList.size()
					- closeCount, cost));
		} finally {
			running = false;
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
		dto.setStatus(SessionHisDto.STATUS_UP);
		return dto;
	}

	public void setSessionHisService(SessionHisService sessionHisService) {
		this.sessionHisService = sessionHisService;
	}
}
