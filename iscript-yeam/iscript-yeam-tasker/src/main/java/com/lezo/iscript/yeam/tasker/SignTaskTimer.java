package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.tasker.service.TaskPriorityService;

public class SignTaskTimer {
	private static Logger log = Logger.getLogger(SignTaskTimer.class);
	private static volatile boolean running = false;
	@Autowired
	private TaskPriorityService taskPriorityService;

	public void run() {
		if (running) {
			log.warn("SignTaskTimer is working...");
			return;
		}
		try {
			List<Long> taskIds = new ArrayList<Long>();
			taskIds.add(3L);
			taskIds.add(4L);
			int affect = taskPriorityService.batchUpdate(taskIds, TaskConstant.TASK_NEW);
			log.info("<signer>.update sign task:" + affect);
			running = true;
		} finally {
			running = false;
		}

	}

	public void setTaskPriorityService(TaskPriorityService taskPriorityService) {
		this.taskPriorityService = taskPriorityService;
	}
}
