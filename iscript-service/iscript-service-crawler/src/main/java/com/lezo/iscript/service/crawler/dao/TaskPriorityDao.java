package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;

public interface TaskPriorityDao {
	void batchInsert(@Param(value = "dtoList") List<TaskPriorityDto> dtoList);

	int batchUpdateStatusByIds(@Param(value = "taskIds") List<Long> taskIds, @Param(value = "status") int status);

	List<TaskPriorityDto> getTaskPriorityDtosByType(@Param(value = "type") String type, @Param(value = "level") int level, @Param(value = "status") int status, @Param(value = "limit") int limit);

	List<TaskPriorityDto> getTaskTypeLevels(@Param(value = "typeList") List<String> typeList, @Param(value = "status") int status);

	Integer deleteTaskPriorityDtos(@Param(value = "type") String type, @Param(value = "status") int status);
}
