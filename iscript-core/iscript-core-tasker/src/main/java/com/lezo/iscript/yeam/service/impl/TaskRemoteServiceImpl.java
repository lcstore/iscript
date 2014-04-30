package com.lezo.iscript.yeam.service.impl;

import java.util.List;

import com.lezo.iscript.yeam.service.TaskRemoteService;
import com.lezo.iscript.yeam.tasker.task.TaskContainer;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskRemoteServiceImpl implements TaskRemoteService {

	@Override
	public void addTasks(List<TaskWritable> taskList) throws IllegalArgumentException {
		TaskContainer.getInstance().add(taskList);
	}

	@Override
	public int updateTasks(List<Long> taskIds, int status, String handler) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

}
