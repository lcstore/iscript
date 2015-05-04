package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.ProxySeedDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProxySeedService;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ProxySeedStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(ProxySeedStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;

	public ProxySeedStrategy() {
		StrategyTimer task = new StrategyTimer();
		this.timer = new Timer(getName());
		this.timer.schedule(task, 1 * 60 * 1000, 1 * 60 * 60 * 1000);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_SUCCESS != rWritable.getStatus()) {
			return;
		}
	}

	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return (StorageBuffer<TaskPriorityDto>) StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	@Override
	public void close() throws IOException {
		this.timer.cancel();
		this.timer = null;
		logger.info("close " + getName() + " strategy..");
	}

	private class StrategyTimer extends TimerTask {
		private ProxySeedService proxySeedService = SpringBeanUtils.getBean(ProxySeedService.class);
		private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);

		public StrategyTimer() {
		}

		public void run() {
			if (running) {
				logger.warn("StrategyTimer is working...");
				return;
			}
			long start = System.currentTimeMillis();
			try {
				logger.info("StrategyTimer is start...");
				running = true;
				List<ProxySeedDto> seedList = proxySeedService.getProxySeedDtoByFromId(0L, Integer.MAX_VALUE);
				List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
				JSONObject argsObject = new JSONObject();
				JSONUtils.put(argsObject, "strategy", getName());
				String taskId = UUID.randomUUID().toString();
				JSONUtils.put(argsObject, "bid", taskId);
				JSONUtils.put(argsObject, "retry", 0);
				String type = "ConfigProxySeedHandler";
				for (ProxySeedDto seedDto : seedList) {
					JSONUtils.put(argsObject, "CreateUrlsFun", seedDto.getCreateUrlsFun());
					JSONUtils.put(argsObject, "DecodePageFun", seedDto.getDecodePageFun());
					JSONUtils.put(argsObject, "seedId", seedDto.getId());
					TaskPriorityDto taskDto = createPriorityDto(seedDto.getUrl(), type, argsObject);
					taskList.add(taskDto);
				}
				taskPriorityService.batchInsert(taskList);
				logger.info("Offer task:{},size:{}", type, taskList.size());
			} catch (Exception ex) {
				logger.warn(ExceptionUtils.getStackTrace(ex));
			} finally {
				long cost = System.currentTimeMillis() - start;
				logger.info("StrategyTimer is done.cost:{}", cost);
				running = false;
			}
		}

		private TaskPriorityDto createPriorityDto(String url, String type, JSONObject argsObject) {
			String taskId = JSONUtils.getString(argsObject, "bid");
			taskId = taskId == null ? UUID.randomUUID().toString() : taskId;
			TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
			taskPriorityDto.setBatchId(taskId);
			taskPriorityDto.setType(type);
			taskPriorityDto.setUrl(url);
			taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
			taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
			taskPriorityDto.setCreatTime(new Date());
			taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
			taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
			JSONObject paramObject = JSONUtils.getJSONObject(argsObject.toString());
			paramObject.remove("bid");
			paramObject.remove("type");
			paramObject.remove("url");
			paramObject.remove("level");
			paramObject.remove("src");
			paramObject.remove("ctime");
			if (taskPriorityDto.getLevel() == null) {
				taskPriorityDto.setLevel(0);
			}
			taskPriorityDto.setParams(paramObject.toString());
			return taskPriorityDto;
		}
	}

}
