package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.Batch;
import com.lezo.iscript.service.crawler.dto.SessionHisDto;

public interface SessionHisDao {
	void batchInsert(List<SessionHisDto> dtoList);

	void batchUpdate(@Batch List<SessionHisDto> dtoList);

	List<SessionHisDto> getSessionHisDtos(@Param(value = "sessionIds") List<String> sessionIds);

	void updateUpSessionToInterrupt();

	List<SessionHisDto> getSessionHisDtosByUpdateTime(@Param(value = "updateTime") Date afterCreateTime);

	void updateSessionByStatus(@Param("fromStatus") int fromStatus, @Param("toStatus") int toStatus);
}
