package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import com.lezo.iscript.common.Batch;
import com.lezo.iscript.service.crawler.dto.SessionDailyDto;

public interface SessionDailyDao {
	void batchInsert(List<SessionDailyDto> dtoList);

	void batchUpdate(@Batch List<SessionDailyDto> dtoList);
}
