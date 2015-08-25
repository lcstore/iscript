package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.BrandDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.BrandService;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class AnccBarCodeStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(AnccBarCodeStrategy.class);
	// private static final String DEFAULT_DETECT_URL = "http://www.baidu.com/";
	private static volatile boolean running = false;
	private Timer timer;

	public AnccBarCodeStrategy() {
        // ProxyDetectTimer task = new ProxyDetectTimer();
        // this.timer = new Timer("AnccBarCodeStrategy");
        // this.timer.schedule(task, 1 * 60 * 1000, 100 * 24 * 60 * 60 * 1000);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {

	}

	private class ProxyDetectTimer extends TimerTask {
		private BrandService brandService = SpringBeanUtils.getBean(BrandService.class);
		private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);

		public ProxyDetectTimer() {
		}

		public void run() {
			if (running) {
				logger.warn("ProxyDetectTimer is working...");
				return;
			}
			long start = System.currentTimeMillis();
			Set<String> brandSet = new HashSet<String>();
			int total = 0;
			String taskType = "ConfigAnccBarCode";
			try {
				logger.info("add taskType:" + taskType + " is start...");
				Long fromId = 0L;
				int limit = 500;
				while (true) {
					List<BrandDto> hasList = brandService.getBrandDtoFromId(fromId, limit);
					String taskId = UUID.randomUUID().toString();
					List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(hasList.size());
					for (BrandDto hasDto : hasList) {
						String name = hasDto.getBrandName().trim();
						if (brandSet.contains(name)) {
							continue;
						}
						brandSet.add(name);
						TaskPriorityDto dto = toPriorityDto(hasDto, taskType, taskId);
						taskList.add(dto);
					}
					taskPriorityService.batchInsert(taskList);
					total += taskList.size();
					logger.info("insert task:" + taskType + ",count:" + taskList.size());
					if (hasList.size() < limit) {
						break;
					}
					for (BrandDto has : hasList) {
						if (fromId < has.getId()) {
							fromId = has.getId();
						}
					}
				}
				running = true;
			} catch (Exception ex) {
				logger.warn(ExceptionUtils.getStackTrace(ex));
			} finally {
				long cost = System.currentTimeMillis() - start;
				String msg = String.format("add task.taskType:%s,total:%s,cost:%s", taskType, total, cost);
				logger.info(msg);
				running = false;
			}

		}

		private TaskPriorityDto toPriorityDto(Object source, String taskType, String taskId) {
			JSONObject argsObject = new JSONObject();
			BrandDto brandDto = (BrandDto) source;
			JSONUtils.put(argsObject, "id", brandDto.getId());
			JSONUtils.put(argsObject, "searchKey", brandDto.getBrandName().trim());
			TaskPriorityDto taskDto = new TaskPriorityDto();
			taskDto.setBatchId(taskId);
			taskDto.setType(taskType);
			String sUrl = "";
			taskDto.setUrl(sUrl);
			taskDto.setLevel(1);
			taskDto.setSource("tasker");
			taskDto.setCreatTime(new Date());
			taskDto.setStatus(TaskConstant.TASK_NEW);
			taskDto.setParams(argsObject.toString());
			return taskDto;
		}

	}

	@Override
	public void close() throws IOException {
		this.timer.cancel();
		this.timer = null;
		logger.info("close " + getName() + " strategy..");
	}
}
