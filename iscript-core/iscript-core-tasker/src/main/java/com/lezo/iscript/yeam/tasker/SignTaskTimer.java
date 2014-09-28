package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.yeam.task.TaskConstant;

public class SignTaskTimer {
	private static final String KEY_SIGN_ID = "sign_id";
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
			Set<Long> idSet = getGlobalIdSet();
			if (idSet.isEmpty()) {
				idSet.add(3L);
				idSet.add(4L);
				idSet.add(6L);
				idSet.add(7L);
				log.warn("Not found[" + KEY_SIGN_ID + "].load hard code ids.");
			}
			List<Long> taskIds = new ArrayList<Long>(idSet);
			int affect = taskPriorityService.batchUpdate(taskIds, TaskConstant.TASK_NEW);
			log.info("<signer>.update sign task:" + affect);
			running = true;
		} finally {
			running = false;
		}

	}

	private Set<Long> getGlobalIdSet() {
		Set<Long> idSet = new HashSet<Long>();
		String idString = System.getProperty(KEY_SIGN_ID);
		if (idString == null) {
			idString = System.getenv(KEY_SIGN_ID);
			log.info("find[" + KEY_SIGN_ID + "] in env:" + idString);
		} else {
			log.info("find[" + KEY_SIGN_ID + "] in property:" + idString);
		}
		if (idString != null) {
			String[] idArray = idString.split(",");
			if (idArray != null) {
				for (String idValue : idArray) {
					idSet.add(Long.valueOf(idValue.trim()));
				}
			}
		}
		return idSet;
	}

	public void setTaskPriorityService(TaskPriorityService taskPriorityService) {
		this.taskPriorityService = taskPriorityService;
	}
}
