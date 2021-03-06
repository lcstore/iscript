package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class JdBrandShopStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(JdBrandShopStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;

	public JdBrandShopStrategy() {
//		CreateTaskTimer task = new CreateTaskTimer();
//		this.timer = new Timer("CreateTaskTimer");
//		this.timer.schedule(task, 60 * 1000, 10 * 24 * 60 * 60 * 1000);
	}

	private class CreateTaskTimer extends TimerTask {
		private Map<String, Set<String>> typeMap;
		private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);

		public CreateTaskTimer() {
			typeMap = new HashMap<String, Set<String>>();
			Set<String> urlSet = new HashSet<String>();
			int maxCout = 110000;
			for (int i = 1; i <= maxCout; i++) {
				urlSet.add("http://www.jd.com/pinpai/" + i + ".html?enc=utf-8&vt=3#filter");
			}
			typeMap.put("ConfigJdBrandShop", urlSet);
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
				JSONObject argsObject = new JSONObject();
				JSONUtils.put(argsObject, "strategy", getName());
				int total = 0;
				List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
				for (Entry<String, Set<String>> entry : typeMap.entrySet()) {
					String taskId = UUID.randomUUID().toString();
					JSONUtils.put(argsObject, "bid", taskId);
					String type = entry.getKey();
					for (String url : entry.getValue()) {
						TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
						taskList.add(taskDto);
					}
					if (taskList.size() >= 1000) {
						total += taskList.size();
						taskPriorityService.batchInsert(taskList);
						logger.info("Offer task:{},size:{}", type, taskList.size());
						taskList.clear();
					}
				}
				total += taskList.size();
				taskPriorityService.batchInsert(taskList);
				logger.info("Offer task,total:{}", total);
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
		if (rWritable.getType().indexOf("BrandList") > 0) {
			JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
			JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
			JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
			try {
				argsObject.remove("name@client");
				argsObject.remove("target");
				addOthers(rWritable, rsObject, argsObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (rWritable.getType().indexOf("BrandShop") > 0) {
			JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
			JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
			JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
			try {
				argsObject.remove("name@client");
				argsObject.remove("target");
				addNextTasks(rsObject, argsObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void addOthers(ResultWritable rWritable, JSONObject rsObject, JSONObject argsObject) throws JSONException {
		JSONArray dataArray = JSONUtils.get(rsObject, "dataList");
		if (dataArray == null) {
			return;
		}
		int len = dataArray.length();
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>(len * 2);
		JSONObject oParamObject = JSONUtils.getJSONObject(argsObject.toString());
		String url = JSONUtils.getString(argsObject, "url");
		JSONUtils.put(oParamObject, "fromUrl", url);
		String productType = rWritable.getType().replace("BrandList", "BrandShop");
		String argsString = oParamObject.toString();
		for (int i = 0; i < len; i++) {
			JSONObject bObject = dataArray.getJSONObject(i);
			JSONObject paramObject = new JSONObject(argsString);
			String brandUrl = JSONUtils.getString(bObject, "brandUrl");
			bObject.remove("brandUrl");
			Iterator<?> it = bObject.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				JSONUtils.put(paramObject, key, JSONUtils.get(bObject, key));
			}
			TaskPriorityDto taskPriorityDto = createPriorityDto(brandUrl, productType, paramObject);
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);

	}

	private void addNextTasks(JSONObject rsObject, JSONObject argsObject) throws Exception {
		JSONArray nextArray = JSONUtils.get(rsObject, "nextList");
		if (nextArray == null) {
			return;
		}
		String type = JSONUtils.getString(argsObject, "type");
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
		JSONUtils.put(argsObject, "fromUrl", JSONUtils.getString(argsObject, "url"));
		for (int i = 0; i < nextArray.length(); i++) {
			String nextUrl = nextArray.getString(i);
			TaskPriorityDto taskPriorityDto = createPriorityDto(nextUrl, type, argsObject);
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);
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
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
		logger.info("close " + getName() + " strategy..");
	}
}