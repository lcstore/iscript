package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
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

public class SignOnStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(SignOnStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;
	private CreateTaskTimer task = new CreateTaskTimer();

	public SignOnStrategy() {
		this.timer = new Timer("CreateTaskTimer");
		this.timer.schedule(task, 5 * 60 * 1000, 24 * 60 * 60 * 1000);
	}

	public Date getSignDate(int addDay, int hour, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.add(Calendar.DAY_OF_MONTH, addDay);
		return c.getTime();
	}

	private class CreateTaskTimer extends TimerTask {
		private Map<String, Set<JSONObject>> typeMap;

		public CreateTaskTimer() {
			typeMap = new HashMap<String, Set<JSONObject>>();
			Set<JSONObject> paramSet = new HashSet<JSONObject>();
			JSONObject argsObject = new JSONObject();
			JSONUtils.put(argsObject, "user", "dlinked@126.com");
			JSONUtils.put(argsObject, "pwd", "dl1234");
			paramSet.add(argsObject);
			argsObject = new JSONObject();
			JSONUtils.put(argsObject, "user", "dlinked1001@126.com");
			JSONUtils.put(argsObject, "pwd", "dl1234");
			paramSet.add(argsObject);
			argsObject = new JSONObject();
			JSONUtils.put(argsObject, "user", "dlinked1002@126.com");
			JSONUtils.put(argsObject, "pwd", "dl1234");
			paramSet.add(argsObject);
			argsObject = new JSONObject();
			JSONUtils.put(argsObject, "user", "pis1001@163.com");
			JSONUtils.put(argsObject, "pwd", "pis1234");
			paramSet.add(argsObject);
			argsObject = new JSONObject();
			JSONUtils.put(argsObject, "user", "pis1002@163.com");
			JSONUtils.put(argsObject, "pwd", "pis1234");
			paramSet.add(argsObject);

			typeMap.put("ConfigHuihuiSigner", paramSet);
		}

		@Override
		public void run() {
			if (running) {
				logger.warn("CreateTaskTimer is working...");
				return;
			}
			long start = System.currentTimeMillis();
			try {
				logger.info("CreateTaskTimer is start...");
				running = true;
				List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
				String taskId = UUID.randomUUID().toString();
				for (Entry<String, Set<JSONObject>> entry : typeMap.entrySet()) {
					String type = entry.getKey();
					for (JSONObject paramObject : entry.getValue()) {
						JSONUtils.put(paramObject, "bid", taskId);
						JSONUtils.put(paramObject, "strategy", getName());
						TaskPriorityDto taskDto = createPriorityDto("http://www.huihui.cn/login", type, paramObject);
						taskList.add(taskDto);
					}
					getTaskPriorityDtoBuffer().addAll(taskList);
					logger.info("Offer task:{},size:{}", type, taskList.size());
				}
			} catch (Exception ex) {
				logger.warn(ExceptionUtils.getStackTrace(ex));
			} finally {
				long cost = System.currentTimeMillis() - start;
				logger.info("CreateTaskTimer is done.cost:{}", cost);
				running = false;
			}
		}
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
		if (rWritable.getType().indexOf("PromotList") > 0) {
			logger.info(rWritable.getResult());
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

	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return (StorageBuffer<TaskPriorityDto>) StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	@Override
	public void close() throws IOException {
		this.timer.cancel();
		this.timer = null;
		logger.info("close " + getName() + " strategy..");
	}
}