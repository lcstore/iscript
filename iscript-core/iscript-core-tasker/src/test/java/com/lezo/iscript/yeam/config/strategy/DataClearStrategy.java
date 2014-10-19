package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.TypeConfigDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.service.crawler.service.TypeConfigService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class DataClearStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(DataClearStrategy.class);
	private static volatile boolean running = false;
	private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);
	private TypeConfigService typeConfigService = SpringBeanUtils.getBean(TypeConfigService.class);
	private Timer timer;

	public DataClearStrategy() {
		ClearTaskTimer task = new ClearTaskTimer();
		this.timer = new Timer("DataClearTimer");
		this.timer.schedule(task, 1 * 60 * 1000, 2 * 60 * 60 * 1000);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {

	}

	private class ClearTaskTimer extends TimerTask {
		private String tasker = "tasker";

		public void run() {
			if (running) {
				logger.warn("ClearTaskTimer is working...");
				return;
			}
			List<TypeConfigDto> typeConfigList = typeConfigService.getTypeConfigDtos(tasker, null);
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
					Integer count = taskPriorityService.deleteTaskPriorityDtos(typeConfigDto.getType(), TaskConstant.TASK_CACHER);
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

	}

	@Override
	public void close() throws IOException {
		this.timer.cancel();
		this.timer = null;
		logger.info("close " + getName() + " strategy..");
	}
}
