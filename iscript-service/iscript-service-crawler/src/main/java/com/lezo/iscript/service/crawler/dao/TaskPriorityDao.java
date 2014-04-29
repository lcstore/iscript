package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;

public interface TaskPriorityDao {
	void batchInsert(List<TaskPriorityDto> dtoList);

	int batchUpdate(List<Long> taskIds, int status);

	List<TaskPriorityDto> getTaskPriorityDtos(String type, int level, int status, int limit);

	List<TaskPriorityDto> getTaskTypeLevels(List<String> typeList, int status);
}
