package com.lezo.iscript.service.crawler.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.TaskConfigDao;
import com.lezo.iscript.service.crawler.dto.TaskConfigDto;
import com.lezo.iscript.service.crawler.service.TaskConfigService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class TaskConfigServiceImpl implements TaskConfigService {
	@Autowired
	private TaskConfigDao taskConfigDao;

	@Override
	public void batchInsert(List<TaskConfigDto> dtoList) {
		BatchIterator<TaskConfigDto> it = new BatchIterator<TaskConfigDto>(dtoList);
		while(it.hasNext()){
			taskConfigDao.batchInsert(it.next());
		}
	}

	@Override
	public void updateOne(TaskConfigDto configDto) {
		taskConfigDao.updateOne(configDto);
	}

	@Override
	public List<TaskConfigDto> getTaskConfigDtos(Date afterStamp, Integer status) {
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
