package com.lezo.iscript.yeam.service;

import java.util.List;

import com.lezo.iscript.yeam.writable.TaskWritable;

public interface TaskRemoteService {

	void addTasks(List<TaskWritable> taskList) throws IllegalArgumentException;

	int updateTasks(List<Long> taskIds, int status,String handler) throws IllegalArgumentException;
}
