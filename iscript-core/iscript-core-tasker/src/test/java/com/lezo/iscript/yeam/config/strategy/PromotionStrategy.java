package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.URLUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class PromotionStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(PromotionStrategy.class);
	private static Map<String, String> typeMap = new HashMap<String, String>();
	private static volatile boolean running = false;
	private Timer timer;

	static {
		typeMap.put("jd.com-product", "ConfigJdProduct");
		typeMap.put("jd.com-promotion", "ConfigJdPromotion");
		typeMap.put("yhd.com-product", "ConfigYhdProduct");
		typeMap.put("yhd.com-promotion", "ConfigYhdPromotion");
	}

	public PromotionStrategy() {
		CreateTaskTimer task = new CreateTaskTimer();
		this.timer = new Timer("CreateTaskTimer");
		this.timer.schedule(task, 60 * 1000, 2 * 60 * 60 * 1000);
	}

	private class CreateTaskTimer extends TimerTask {
		private Map<String, Set<String>> typeMap;

		public CreateTaskTimer() {
			typeMap = new HashMap<String, Set<String>>();
			Set<String> urlSet = new HashSet<String>();
			urlSet.add("http://xuan.jd.com/youhui/1-0-0-0-1.html");
			urlSet.add("http://xuan.jd.com/youhui/2-0-0-0-1.html");
			urlSet.add("http://xuan.jd.com/youhui/3-0-0-0-1.html");
			urlSet.add("http://xuan.jd.com/youhui/4-0-0-0-1.html");
			urlSet.add("http://xuan.jd.com/youhui/5-0-0-0-1.html");
			urlSet.add("http://xuan.jd.com/youhui/6-0-0-0-1.html");
			urlSet.add("http://xuan.jd.com/youhui/7-0-0-0-1.html");

			typeMap.put("ConfigJdPromotList", urlSet);

			urlSet = new HashSet<String>();
			urlSet.add("http://www.yhd.com");
			typeMap.put("ConfigYhdPromotList", urlSet);
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
				JSONObject argsObject = new JSONObject();
				JSONUtils.put(argsObject, "strategy", getName());
				for (Entry<String, Set<String>> entry : typeMap.entrySet()) {
					String taskId = UUID.randomUUID().toString();
					JSONUtils.put(argsObject, "bid", taskId);
					String type = entry.getKey();
					for (String url : entry.getValue()) {
						TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
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
			JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
			JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
			JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
			try {
				argsObject.remove("name@client");
				addOthers(rsObject, argsObject);
				addNextTasks(rsObject, argsObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void addOthers(JSONObject rsObject, JSONObject argsObject) throws JSONException {
		JSONArray dataArray = JSONUtils.get(rsObject, "dataList");
		if (dataArray == null) {
			return;
		}
		int len = dataArray.length();
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>(len * 2);
		JSONObject oParamObject = JSONUtils.getJSONObject(argsObject.toString());
		String url = JSONUtils.getString(argsObject, "url");
		oParamObject.remove("strategy");
		JSONUtils.put(oParamObject, "fromUrl", url);
		String productType = getType(url, "product");
		String promotionType = getType(url, "promotion");
		for (int i = 0; i < len; i++) {
			String sUrl = dataArray.getString(i);
			TaskPriorityDto taskPriorityDto = createPriorityDto(sUrl, productType, oParamObject);
			dtoList.add(taskPriorityDto);
			taskPriorityDto = createPriorityDto(sUrl, promotionType, oParamObject);
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);

	}

	private String getType(String url, String name) {
		String host = URLUtils.getRootHost(url);
		String key = host + "-" + name;
		return typeMap.get(key);
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
		this.timer.cancel();
		this.timer = null;
		logger.info("close " + getName() + " strategy..");
	}
}