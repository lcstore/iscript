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

@Service
public class TaskPriorityServiceImpl implements TaskPriorityService {
	private static final int EXECUTE_SIZE = 200;
	@Autowired
	private TaskPriorityDao taskPriorityDao;

	@Override
	public void batchInsert(List<TaskPriorityDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		int total = dtoList.size();
		int fromIndex = 0;
		while (fromIndex < total) {
			int toIndex = fromIndex + EXECUTE_SIZE;
			toIndex = toIndex > total ? total : toIndex;
			List<TaskPriorityDto> subList = dtoList.subList(fromIndex, toIndex);
			taskPriorityDao.batchInsert(subList);
			fromIndex = toIndex;
		}
	}

	@Override
	public int batchUpdate(List<Long> taskIds, int status) {
		int affect = 0;
		if (CollectionUtils.isEmpty(taskIds)) {
			return affect;
		}
		int total = taskIds.size();
		int fromIndex = 0;
		while (fromIndex < total) {
			int toIndex = fromIndex + EXECUTE_SIZE;
			toIndex = toIndex > total ? total : toIndex;
			List<Long> subList = taskIds.subList(fromIndex, toIndex);
			affect += taskPriorityDao.batchUpdate(subList, status);
			fromIndex = toIndex;
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
