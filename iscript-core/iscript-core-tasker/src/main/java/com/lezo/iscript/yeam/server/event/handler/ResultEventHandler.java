package com.lezo.iscript.yeam.server.event.handler;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.result.ResultHandlerCaller;
import com.lezo.iscript.yeam.result.ResultHandlerWoker;
import com.lezo.iscript.yeam.server.event.RequestEvent;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ResultEventHandler extends AbstractEventHandler {
	private static Logger logger = LoggerFactory.getLogger(ResultEventHandler.class);

	protected void doHandle(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null || ioRequest.getData() == null) {
			return;
		}
		Object rsObject = ioRequest.getData();
		@SuppressWarnings("unchecked")
		List<ResultWritable> rWritables = (List<ResultWritable>) rsObject;
		if (!CollectionUtils.isEmpty(rWritables)) {
			add2Session(event.getSession(), rWritables);
			addTrackSession(event.getSession());
			ResultHandlerCaller caller = ResultHandlerCaller.getInstance();
			caller.execute(new ResultHandlerWoker(rWritables));
			String msg = String.format("add %d result to caller.active woker:%d,in Queue worker:%d", rWritables.size(),
					caller.getExecutor().getActiveCount(), caller.getExecutor().getQueue().size());
			logger.info(msg);
		}
	}

	private void add2Session(IoSession session, List<ResultWritable> rWritables) {
		int success = 0;
		int fail = 0;
		for (ResultWritable rw : rWritables) {
			if (ResultWritable.RESULT_SUCCESS == rw.getStatus()) {
				success++;
			} else {
				fail++;
			}
		}
		add2Attribute(session, SessionHisDto.SUCCESS_NUM, success);
		add2Attribute(session, SessionHisDto.FAIL_NUM, fail);
	}

	private void addTrackSession(IoSession session) {
		String key = SessionHisDto.SAVE_STAMP;
		Long stamp = (Long) session.getAttribute(key);
		long cost = System.currentTimeMillis() - stamp;
		if (cost >= SessionHisDto.MAX_SAVE_INTERVAL) {
			session.setAttribute(key, System.currentTimeMillis());
			SessionHisDto trackDto = getSessionHisDto(session);
			if (!StringUtils.isEmpty(trackDto.getClienName())) {
				logger.info(String.format("track: %s", trackDto));
				trackDto.setStatus(SessionHisDto.STATUS_UP);
				StorageBufferFactory.getStorageBuffer(SessionHisDto.class).add(trackDto);
			} else {
				logger.warn(String.format("track session.can not found name for sessionId:%s", trackDto.getSessionId()));
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

	public void add2Attribute(IoSession session, String key, int num) {
		int newValue = (Integer) session.getAttribute(key) + num;
		session.setAttribute(key, newValue);
	}

	@Override
	protected boolean isAccept(RequestEvent event) {
		IoRequest ioRequest = getIoRequest(event);
		if (ioRequest == null) {
			return false;
		}
		Object dataObject = ioRequest.getData();
		if (dataObject == null) {
			return false;
		}
		if (dataObject instanceof List) {
			return true;
		}
		return false;
	}
}