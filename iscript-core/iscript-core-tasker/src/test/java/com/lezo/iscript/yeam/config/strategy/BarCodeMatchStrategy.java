package com.lezo.iscript.yeam.config.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
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

public class BarCodeMatchStrategy implements ResultStrategy {
	private static Logger logger = LoggerFactory.getLogger(BarCodeMatchStrategy.class);
	private static volatile boolean running = false;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_SUCCESS != rWritable.getStatus()) {
			return;
		}
		JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
		JSONObject argsObject = JSONUtils.get(gObject, "args");
		JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
		JSONArray dArray = JSONUtils.get(rsObject, "dataList");
		if (dArray == null) {
			return;
		}
		JSONUtils.put(argsObject, "retry", 0);
		int size = dArray.length();
		try {
			List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(size);
			for (int i = 0; i < size; i++) {
				JSONObject itemObject = dArray.getJSONObject(i);
				String type = getProductType(JSONUtils.getInteger(itemObject, "siteId"));
				String url = JSONUtils.getString(itemObject, "productUrl");
				if (StringUtils.isEmpty(type) || StringUtils.isEmpty(url)) {
					logger.warn("type:{},url:{},data:{}", type, url, itemObject);
				} else {
					TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
					taskList.add(taskDto);
				}
			}
			getTaskPriorityDtoBuffer().addAll(taskList);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private String getProductType(Integer siteId) {
		if (1001 == siteId) {
			return "ConfigJdProduct";
		} else if (1002 == siteId) {
			return "ConfigYhdProduct";
		} else if (1003 == siteId) {
			return "ConfigAmazonProduct";
		}
		return null;
	}

	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return (StorageBuffer<TaskPriorityDto>) StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
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
