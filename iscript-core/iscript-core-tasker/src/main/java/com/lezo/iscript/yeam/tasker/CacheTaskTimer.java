package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.dto.TypeConfigDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.service.crawler.service.TypeConfigService;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.tasker.cache.TaskQueue;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class CacheTaskTimer {
	private static final int ONE_FETCH_SIZE = 100;
	private static Logger log = Logger.getLogger(CacheTaskTimer.class);
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
				typeMap.put(dto.getType(), dto);
			}
			List<String> typeList = new ArrayList<String>(typeMap.keySet());
			List<TaskPriorityDto> typeLevelList = taskPriorityService
					.getTaskTypeLevels(typeList, TaskConstant.TASK_NEW);
			log.info("add task.tasker[" + tasker + "],config size:" + typeConfigList.size() + ",typeLevel count:"
					+ typeLevelList.size());
			for (TaskPriorityDto dto : typeLevelList) {
				cacheTasks(typeMap.get(dto.getType()), dto.getLevel());
			}
		} finally {
			running = false;
		}

	}

	private void cacheTasks(TypeConfigDto typeConfigDto, int level) {
		TaskQueue queue = TaskCacher.getInstance().getQueue(typeConfigDto.getType());
		int hasSize = queue.size();
		if (hasSize > typeConfigDto.getMinSize()) {
			return;
		}
		int addSize = typeConfigDto.getMaxSize() - hasSize;
		int remain = addSize;
		int limit = ONE_FETCH_SIZE;
		boolean empty = false;
		while (remain > 0) {
			if (remain < ONE_FETCH_SIZE) {
				limit = remain;
			}
			List<TaskPriorityDto> taskList = taskPriorityService.getTaskPriorityDtos(typeConfigDto.getType(), level,
					TaskConstant.TASK_NEW, limit);
			List<Long> taskIds = new ArrayList<Long>(taskList.size());
			for (TaskPriorityDto dto : taskList) {
				TaskWritable task = getTaskWritable(dto);
				if (queue.offer(task, dto.getLevel())) {
					taskIds.add(dto.getTaskId());
				}
			}
			remain -= taskPriorityService.batchUpdate(taskIds, TaskConstant.TASK_CACHER);
			if (taskList.size() < limit) {
				empty = true;
				break;
			}
		}
		addSize = addSize - remain;
		String typeMsg = empty ? ".no more tasks." : "";
		log.info("tasker[" + tasker + "],type[" + typeConfigDto.getType() + ":" + level + "].add:" + addSize
				+ ",queue:" + queue.size() + typeMsg);
	}

	private TaskWritable getTaskWritable(TaskPriorityDto dto) {
		TaskWritable task = new TaskWritable();
		task.setId(dto.getTaskId());
		task.put("bid", dto.getBatchId());
		task.put("type", dto.getType());
		task.put("url", dto.getUrl());
		task.put("level", dto.getLevel());
		String param = dto.getParams();
		try {
			param = getStandardParam(param);
			JSONObject argsObject = new JSONObject(param);
			Iterator<?> it = argsObject.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				Object value = argsObject.get(key);
				task.put(key, value);
			}
		} catch (JSONException e) {
		}
		return task;
	}

	private String getStandardParam(String param) {
		if (StringUtils.isEmpty(param)) {
			return "{}";
		}
		param = param.trim();
		if (!param.startsWith("{")) {
			param = "{" + param;
		}
		if (!param.endsWith("}")) {
			param = param + "}";
		}
		return param;
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
