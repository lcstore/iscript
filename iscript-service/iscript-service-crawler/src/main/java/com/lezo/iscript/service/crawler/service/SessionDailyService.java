package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.SessionDailyDto;

public interface SessionDailyService {
	void batchInsertSessionDailyDtos(List<SessionDailyDto> dtoList);

	void batchUpdateSessionDailyDtos(List<SessionDailyDto> dtoList);
	
	void summaryDailySession();
}

