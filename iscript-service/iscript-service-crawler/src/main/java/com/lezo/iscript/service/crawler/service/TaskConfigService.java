package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.TaskConfigDto;

public interface TaskConfigService {
	void batchInsert(List<TaskConfigDto> dtoList);

	void updateOne(TaskConfigDto configDto);

	List<TaskConfigDto> getTaskConfigDtos(Date afterStamp, Integer status);

	TaskConfigDto getTaskConfig(String configType);

	void deleteConfig(String type);
}
