package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dao.SessionDailyDao;
import com.lezo.iscript.service.crawler.dto.SessionDailyDto;
import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.service.crawler.service.SessionDailyService;
import com.lezo.iscript.service.crawler.service.SessionHisService;
import com.lezo.iscript.utils.BatchIterator;

public class SessionDailyServiceImpl implements SessionDailyService {
	@Autowired
	private SessionDailyDao sessionDailyDao;
	@Autowired
	private SessionHisService sessionHisService;

	@Override
	public void batchInsertSessionDailyDtos(List<SessionDailyDto> dtoList) {
		BatchIterator<SessionDailyDto> it = new BatchIterator<SessionDailyDto>(dtoList);
		while (it.hasNext()) {
			sessionDailyDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateSessionDailyDtos(List<SessionDailyDto> dtoList) {
		BatchIterator<SessionDailyDto> it = new BatchIterator<SessionDailyDto>(dtoList);
		while (it.hasNext()) {
			sessionDailyDao.batchUpdate(dtoList);
		}
	}

	@Override
	public void summaryDailySession() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		Date current = c.getTime();
		List<SessionHisDto> sessionHisList = sessionHisService.getSessionHisDtosByUpdateTime(current);
		Map<String, List<SessionHisDto>> clientMap = toClientSessionMap(sessionHisList);
		for (Entry<String, List<SessionHisDto>> entry : clientMap.entrySet()) {
		}
	}

	private Map<String, List<SessionHisDto>> toClientSessionMap(List<SessionHisDto> sessionHisList) {
		Map<String, List<SessionHisDto>> clientMap = new HashMap<String, List<SessionHisDto>>();
		for (SessionHisDto dto : sessionHisList) {
			List<SessionHisDto> dtoList = clientMap.get(dto.getClienName());
			if (dtoList == null) {
				dtoList = new ArrayList<SessionHisDto>();
				clientMap.put(dto.getClienName(), dtoList);
			}
			dtoList.add(dto);
		}
		return clientMap;
	}

}
