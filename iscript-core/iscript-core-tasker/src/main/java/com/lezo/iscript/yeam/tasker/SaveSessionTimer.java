package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.service.crawler.service.SessionHisService;
import com.lezo.iscript.yeam.server.session.SessionCacher;

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
			List<IoSession> closeList = SessionCacher.getInstance().removeClosed();
			List<SessionHisDto> copyList = new ArrayList<SessionHisDto>();
			for (IoSession session : closeList) {
				SessionHisDto sessionHisDto = getSessionHisDto(session);
				sessionHisDto.setStatus(SessionHisDto.STATUS_DOWN);
				copyList.add(sessionHisDto);
			}
			for (Entry<String, IoSession> entry : SessionCacher.getInstance().entrySet()) {
				IoSession session = entry.getValue();
				SessionHisDto sessionHisDto = getSessionHisDto(session);
				if (!session.isConnected() || session.isClosing()) {
					sessionHisDto.setStatus(SessionHisDto.STATUS_DOWN);
				}
				copyList.add(sessionHisDto);
			}
			sessionHisService.batchSaveSessionHisDtos(copyList);
			long cost = System.currentTimeMillis() - start;
			logger.info(String.format("Save[%s],close:%d,cost:%s", "SessionHisDto", copyList.size(), closeList.size(), cost));
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
