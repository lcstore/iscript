package com.lezo.iscript.yeam.tasker.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.yeam.tasker.dao.TaskConfigDao;
import com.lezo.iscript.yeam.tasker.dto.TaskConfigDto;
import com.lezo.iscript.yeam.tasker.service.TaskConfigService;

@Service
public class TaskConfigServiceImpl implements TaskConfigService {
	private static final int EXECUTE_SIZE = 200;
	@Autowired
	private TaskConfigDao taskConfigDao;

	@Override
	public void batchInsert(List<TaskConfigDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		int total = dtoList.size();
		int fromIndex = 0;
		while (fromIndex < total) {
			int toIndex = fromIndex + EXECUTE_SIZE;
			toIndex = toIndex > total ? total : toIndex;
			List<TaskConfigDto> subList = dtoList.subList(fromIndex, toIndex);
			taskConfigDao.batchInsert(subList);
			fromIndex = toIndex;
		}
	}

	@Override
	public void updateOne(TaskConfigDto configDto) {
		taskConfigDao.updateOne(configDto);
	}

	@Override
	public List<TaskConfigDto> getTaskConfigDtos(Date afterStamp, int status) {
		return taskConfigDao.getTaskConfigDtos(afterStamp, status);
	}

	@Override
	public TaskConfigDto getTaskConfig(String configType) {
		return taskConfigDao.getTaskConfig(configType);
	}

	@Override
	public void deleteConfig(String type) {
		taskConfigDao.deleteConfig(type);
	}

}
