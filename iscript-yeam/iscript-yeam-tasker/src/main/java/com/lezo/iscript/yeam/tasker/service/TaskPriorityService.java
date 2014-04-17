package com.lezo.iscript.yeam.tasker.service;

import java.util.List;

import com.lezo.iscript.yeam.tasker.dto.TaskPriorityDto;

public interface TaskPriorityService {
	void batchInsert(List<TaskPriorityDto> dtoList);

	int batchUpdate(List<Long> taskIds, int status);

	List<TaskPriorityDto> getTaskPriorityDtos(String type, int level, int status, int limit);

	List<TaskPriorityDto> getTaskTypeLevels(List<String> typeList, int status);
}
