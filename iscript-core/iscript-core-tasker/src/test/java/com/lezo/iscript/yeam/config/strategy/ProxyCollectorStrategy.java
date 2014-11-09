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
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ProxyCollectorStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(ProxyCollectorStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;

	public ProxyCollectorStrategy() {
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
		if ("ConfigProxyCollector".equals(rWritable.getType())) {
			JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
			JSONObject argsObject = JSONUtils.get(jObject, "args");
			String rsString = JSONUtils.getString(jObject, "rs");
			List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
			// List<ProxyDetectDto> dtoList = new ArrayList<ProxyDetectDto>();
			try {
				JSONObject rootObject = new JSONObject(rsString);
				// addResults(rootObject, argsObject, dtoList);
				addNextTasks(rootObject, argsObject, taskList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!taskList.isEmpty()) {
				getTaskPriorityDtoBuffer().addAll(taskList);
			}
		}

	}

	private void addNextTasks(JSONObject rootObject, JSONObject argsObject, List<TaskPriorityDto> dtoList) throws Exception {
		JSONArray nextArray = JSONUtils.get(rootObject, "nextList");
		if (nextArray == null) {
			return;
		}

		for (int i = 0; i < nextArray.length(); i++) {
			String nextUrl = nextArray.getString(i);
			TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
			taskPriorityDto.setBatchId(JSONUtils.getString(argsObject, "bid"));
			taskPriorityDto.setType("ConfigProxyCollector");
			taskPriorityDto.setUrl(nextUrl);
			taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
			taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
			taskPriorityDto.setCreatTime(new Date());
			taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
			taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
			argsObject.remove("bid");
			argsObject.remove("type");
			argsObject.remove("url");
			argsObject.remove("level");
			argsObject.remove("src");
			argsObject.remove("ctime");
			if (taskPriorityDto.getLevel() == null) {
				taskPriorityDto.setLevel(0);
			}
			taskPriorityDto.setParams(argsObject.toString());
			dtoList.add(taskPriorityDto);
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
				List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
				JSONObject argsObject = new JSONObject();
				JSONUtils.put(argsObject, "strategy", getName());
				String taskId = UUID.randomUUID().toString();
				JSONUtils.put(argsObject, "bid", taskId);
				String type = "ConfigProxyCollector";
				TaskPriorityDto taskDto = createPriorityDto("", type, argsObject);
				taskList.add(taskDto);
				getTaskPriorityDtoBuffer().addAll(taskList);
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
