package com.lezo.iscript.service.crawler.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.service.crawler.dto.TaskConfigDto;

public interface TaskConfigDao {
	void batchInsert(List<TaskConfigDto> dtoList);

	void updateTaskConfigDto(TaskConfigDto configDto);

	List<TaskConfigDto> getTaskConfigDtos(@Param(value = "afterStamp") Date afterStamp,
			@Param(value = "status") Integer status);

	TaskConfigDto getTaskConfig(String type);

	void deleteConfig(String type);
}
