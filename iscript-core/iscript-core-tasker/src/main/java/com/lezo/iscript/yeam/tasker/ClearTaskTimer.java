package com.lezo.iscript.yeam.tasker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.TypeConfigDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.service.crawler.service.TypeConfigService;
import com.lezo.iscript.yeam.task.TaskConstant;

public class ClearTaskTimer {
	private static Logger log = Logger.getLogger(ClearTaskTimer.class);
	private static volatile boolean running = false;
	private String tasker;
	@Autowired
	private TaskPriorityService taskPriorityService;
	@Autowired
	private TypeConfigService typeConfigService;

	public void run() {
		if (running) {
			log.warn("Task loader is working...");
			return;
		}
		List<TypeConfigDto> typeConfigList = typeConfigService.getEnableTypeConfigDtos(tasker);
		if (CollectionUtils.isEmpty(typeConfigList)) {
			log.info("no type config for tasker:" + tasker);
			return;
		}
		try {
			running = true;
			Map<String, TypeConfigDto> typeMap = new HashMap<String, TypeConfigDto>();
			for (TypeConfigDto dto : typeConfigList) {
				deleteTasks(dto);
			}
		} finally {
			running = false;
		}

	}

	private void deleteTasks(TypeConfigDto typeConfigDto) {
		this.taskPriorityService.deleteTaskPriorityDtos(typeConfigDto.getType(), TaskConstant.TASK_CACHER);
	}

	public void setTasker(String tasker) {
		this.tasker = tasker;
	}

	public void setTaskPriorityService(TaskPriorityService taskPriorityService) {
		this.taskPriorityService = taskPriorityService;
	}

	public void setTypeConfigService(TypeConfigService typeConfigService) {
		this.typeConfigService = typeConfigService;
	}
}
