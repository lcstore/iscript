package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.SessionHisDto;

public interface SessionHisService {
	void batchInsertSessionHisDtos(List<SessionHisDto> dtoList);

	void batchUpdateSessionHisDtos(List<SessionHisDto> dtoList);

	void batchSaveSessionHisDtos(List<SessionHisDto> dtoList);

	List<SessionHisDto> getSessionHisDtos(List<String> sessionIds);

	void updateUpSessionToInterrupt();

	List<SessionHisDto> getSessionHisDtosByUpdateTime(Date afterUpdateTime);

	void updateSessionByStatus(int fromStatus, int toStatus);
}
