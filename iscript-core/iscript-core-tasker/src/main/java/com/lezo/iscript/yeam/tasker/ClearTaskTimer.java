package com.lezo.iscript.yeam.tasker;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.TypeConfigDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.service.crawler.service.TypeConfigService;
import com.lezo.iscript.yeam.task.TaskConstant;

public class ClearTaskTimer {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ClearTaskTimer.class);
	private static volatile boolean running = false;
	private String tasker;
	@Autowired
	private TaskPriorityService taskPriorityService;
	@Autowired
	private TypeConfigService typeConfigService;

	public void run() {
		if (running) {
			logger.warn("ClearTaskTimer is working...");
			return;
		}
		List<TypeConfigDto> typeConfigList = typeConfigService.getEnableTypeConfigDtos(tasker);
		if (CollectionUtils.isEmpty(typeConfigList)) {
			logger.info("no type config for tasker:" + tasker);
			return;
		}
		long fromTimeMillis = System.currentTimeMillis();
		int clearTypeCount = 0;
		try {
			logger.info("ClearTaskTimer is start...");
			running = true;
			for (TypeConfigDto typeConfigDto : typeConfigList) {
				long start = System.currentTimeMillis();
				Integer count = this.taskPriorityService.deleteTaskPriorityDtos(typeConfigDto.getType(), TaskConstant.TASK_CACHER);
				if (count > 0) {
					clearTypeCount++;
				}
				long cost = System.currentTimeMillis() - start;
				logger.info("delect type:{},status:{},count:{},cost:{}", typeConfigDto.getType(), TaskConstant.TASK_CACHER, count, cost);
			}
		} finally {
			running = false;
			long cost = System.currentTimeMillis() - fromTimeMillis;
			logger.info("ClearTaskTimer is done,totalType:{},clearType:{},cost:{}", typeConfigList.size(), clearTypeCount, cost);
		}

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
