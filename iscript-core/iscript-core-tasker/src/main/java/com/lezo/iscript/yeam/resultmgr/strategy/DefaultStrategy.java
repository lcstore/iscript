package com.lezo.iscript.yeam.resultmgr.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class DefaultStrategy implements ResultStrategy {
	private static Logger logger = LoggerFactory.getLogger(DefaultStrategy.class);

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
		JSONObject argsObject = JSONUtils.get(jObject, "args");
		String rsString = JSONUtils.getString(jObject, "rs");
		JSONObject rootObject = JSONUtils.getJSONObject(rsString);
		JSONArray nextArray = JSONUtils.get(rootObject, "nextList");
		if (nextArray == null || nextArray.length() < 1) {
			return;
		}
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
		for (int i = 0; i < nextArray.length(); i++) {
			try {
				String nextUrl = nextArray.getString(i);
				TaskPriorityDto taskPriorityDto = createPriorityDto(nextUrl, rWritable.getType(), argsObject);
				dtoList.add(taskPriorityDto);
			} catch (JSONException e) {
				logger.warn("" + nextArray, e);
			}
		}
		if (!dtoList.isEmpty()) {
			StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class).addAll(dtoList);
			logger.info("Create Next task.type:{},count:{}", rWritable.getType(), dtoList.size());
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
