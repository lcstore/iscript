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

import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class JdListStrategy implements ResultStrategy, Closeable {
	private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);
	private static Logger logger = LoggerFactory.getLogger(JdListStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;

	public JdListStrategy() {
        // CreateTaskTimer task = new CreateTaskTimer();
        // this.timer = new Timer(getName());
        // this.timer.schedule(task, 60 * 1000, 24 * 60 * 60 * 1000);
	}

	private class CreateTaskTimer extends TimerTask {
		private Map<String, Set<String>> typeMap;
		private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);

		public CreateTaskTimer() {
			typeMap = new HashMap<String, Set<String>>();
			Set<String> urlSet = new HashSet<String>();
			urlSet.add("http://list.jd.com/list.html?cat=9987,653,655");
			typeMap.put("ConfigJdList", urlSet);
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
				JSONUtils.put(argsObject, "retry", 0);
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
		if (rWritable.getType().endsWith("JdList")) {
			JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
			JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
			JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
			try {
				argsObject.remove("name@client");
				argsObject.remove("target");
				addNexts(rWritable, rsObject, argsObject);
				addOthers(rWritable, rsObject, argsObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (rWritable.getType().endsWith("JdProduct")) {
			JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
			JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
			JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
			try {
				argsObject.remove("name@client");
				argsObject.remove("target");
				argsObject.remove("fromUrl");
				addNexts(rWritable, rsObject, argsObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void addNexts(ResultWritable rWritable, JSONObject rsObject, JSONObject argsObject) {
		JSONObject rootObject = rsObject;
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
		taskPriorityService.batchInsert(dtoList);
		logger.info("insert nexts task:" + rWritable.getType() + ",count:" + dtoList.size());
	}

	private void addOthers(ResultWritable rWritable, JSONObject rsObject, JSONObject argsObject) throws JSONException {
		JSONArray dataArray = JSONUtils.get(rsObject, "dataList");
		if (dataArray == null) {
			return;
		}
		int len = dataArray.length();
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>(len);
		JSONObject oParamObject = JSONUtils.getJSONObject(argsObject.toString());
		String url = JSONUtils.getString(argsObject, "url");
		JSONUtils.put(oParamObject, "fromUrl", url);
		JSONUtils.put(oParamObject, "bid", UUID.randomUUID().toString());
		String productType = rWritable.getType().replace("JdList", "JdProduct");
		String argsString = oParamObject.toString();
		for (int i = 0; i < len; i++) {
			String nextUrl = dataArray.getString(i);
			JSONObject paramObject = new JSONObject(argsString);
			TaskPriorityDto taskPriorityDto = createPriorityDto(nextUrl, productType, paramObject);
			dtoList.add(taskPriorityDto);
		}
		taskPriorityService.batchInsert(dtoList);
		logger.info("insert task:" + productType + ",count:" + dtoList.size());

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
			taskPriorityDto.setLevel(1);
		}
		taskPriorityDto.setParams(paramObject.toString());
		return taskPriorityDto;
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