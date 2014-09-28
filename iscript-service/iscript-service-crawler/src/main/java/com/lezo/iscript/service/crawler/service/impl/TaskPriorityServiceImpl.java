package com.lezo.iscript.service.crawler.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.TaskPriorityDao;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class TaskPriorityServiceImpl implements TaskPriorityService {
	@Autowired
	private TaskPriorityDao taskPriorityDao;

	@Override
	public void batchInsert(List<TaskPriorityDto> dtoList) {
		BatchIterator<TaskPriorityDto> it = new BatchIterator<TaskPriorityDto>(dtoList);
		while(it.hasNext()){
			taskPriorityDao.batchInsert(it.next());
		}
	}

	@Override
	public int batchUpdate(List<Long> taskIds, int status) {
		int affect=0;
		BatchIterator<Long> it = new BatchIterator<Long>(taskIds);
		while(it.hasNext()){
			affect+=taskPriorityDao.batchUpdate(it.next(),status);
		}
		return affect;
	}

	@Override
	public List<TaskPriorityDto> getTaskPriorityDtos(String type, int level, int status, int limit) {
		if (StringUtils.isEmpty(type) || limit < 1) {
			return Collections.emptyList();
		}
		return taskPriorityDao.getTaskPriorityDtos(type, level, status, limit);
	}

	@Override
	public List<TaskPriorityDto> getTaskTypeLevels(List<String> typeList, int status) {
		if (CollectionUtils.isEmpty(typeList)) {
			return Collections.emptyList();
		}
		return taskPriorityDao.getTaskTypeLevels(typeList, status);
	}

	public void setTaskPriorityDao(TaskPriorityDao taskPriorityDao) {
		this.taskPriorityDao = taskPriorityDao;
	}

}
