package com.lezo.iscript.yeam.tasker.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.yeam.tasker.dto.TaskConfigDto;

public interface TaskConfigService {
	void batchInsert(List<TaskConfigDto> dtoList);

	void updateOne(TaskConfigDto configDto);

	List<TaskConfigDto> getTaskConfigDtos(Date afterStamp, int status);
}
