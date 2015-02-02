package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProxyAddrService;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ProxyDetectStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(ProxyDetectStrategy.class);
	private static final String DEFAULT_DETECT_URL = "http://www.baidu.com/";
	private static volatile boolean running = false;
	private Timer timer;

	public ProxyDetectStrategy() {
		ProxyDetectTimer task = new ProxyDetectTimer();
		this.timer = new Timer("ProxyDetectProducer");
		this.timer.schedule(task, 1 * 60 * 1000, 5 * 60 * 60 * 1000);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_FAIL == rWritable.getStatus()) {
			addRetry(rWritable);
		} else if (ResultWritable.RESULT_SUCCESS == rWritable.getStatus()) {
			// if ("ConfigProxyDetector".equals(rWritable.getType())) {
			// JSONObject jObject =
			// JSONUtils.getJSONObject(rWritable.getResult());
			// JSONObject argsObject = JSONUtils.get(jObject, "args");
			// String rsString = JSONUtils.getString(jObject, "rs");
			// List<ProxyDetectDto> dtoList = new ArrayList<ProxyDetectDto>();
			// try {
			// JSONObject rootObject = new JSONObject(rsString);
			// // addResults(rootObject, argsObject, dtoList);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// if (!dtoList.isEmpty()) {
			// getStorageBuffer().addAll(dtoList);
			// }
			// logger.info("add ProxyDetectDto to buffer,size:" + dtoList.size()
			// + ",total:"
			// + getStorageBuffer().size());
			// }
		}

	}

	private void addResults(JSONObject rootObject, JSONObject argsObject, List<ProxyDetectDto> dtoList) throws Exception {
		JSONObject newObject = rootObject;
		ProxyDetectDto dto = new ProxyDetectDto();
		dto.setId(JSONUtils.getLong(argsObject, "id"));
		dto.setIp(JSONUtils.getLong(argsObject, "ip"));
		dto.setPort(JSONUtils.getInteger(argsObject, "port"));

		dto.setDetector(JSONUtils.getString(newObject, "detector"));
		dto.setStatus(JSONUtils.getInteger(newObject, "status"));
		dto.setCurCost(JSONUtils.getLong(newObject, "cost"));
		dto.setDomain(JSONUtils.getString(newObject, "domain"));
		dto.setUrl(JSONUtils.getString(newObject, "url"));

		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		dtoList.add(dto);
	}

	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return (StorageBuffer<TaskPriorityDto>) StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	private void addRetry(ResultWritable rWritable) {
		JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
		JSONObject argsObject = JSONUtils.get(jObject, "args");
		JSONObject exObject = JSONUtils.get(jObject, "ex");
		String rsString = JSONUtils.getString(jObject, "rs");
		JSONObject rsObject = JSONUtils.getJSONObject(rsString);
		TaskWritable tWritable = new TaskWritable();
		tWritable.setId(rWritable.getTaskId());
		Iterator<?> it = argsObject.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			tWritable.put(key, JSONUtils.getObject(argsObject, key));
		}
		Integer retry = (Integer) tWritable.get("retry");
		if (retry == null) {
			retry = 0;
		} else if (retry >= 3) {
			return;
		}
		tWritable.put("retry", retry + 1);
		Integer level = JSONUtils.getInteger(argsObject, "level");
		level = level == null ? 0 : level;
		logger.warn("retry task:" + tWritable.getId() + ",args:" + new JSONObject(tWritable.getArgs()) + ",rs:" + rsObject + ",ex:" + exObject);
		TaskCacher.getInstance().getQueue(rWritable.getType()).offer(tWritable, level);
	}

	private class ProxyDetectTimer extends TimerTask {
		private ProxyDetectService proxyDetectService = SpringBeanUtils.getBean(ProxyDetectService.class);
		private ProxyAddrService proxyAddrService = SpringBeanUtils.getBean(ProxyAddrService.class);
		private List<Integer> checkStatusList;

		public ProxyDetectTimer() {
			checkStatusList = new ArrayList<Integer>();
			checkStatusList.add(ProxyDetectDto.STATUS_WORK);
			checkStatusList.add(ProxyDetectDto.STATUS_USABLE);
			checkStatusList.add(ProxyDetectDto.STATUS_RETRY);
			// checkStatusList.add(ProxyDetectDto.STATUS_NONUSE);
		}

		public void run() {
			if (running) {
				logger.warn("ProxyDetectTimer is working...");
				return;
			}
			long start = System.currentTimeMillis();
			JSONObject statusObject = new JSONObject();
			String taskId = UUID.randomUUID().toString();
			try {
				logger.info("Detect proxy is start...");
				running = true;
				Date afterTime = null;
				for (int status : checkStatusList) {
					List<ProxyDetectDto> dtoList = proxyDetectService.getProxyDetectDtosFromStatus(status, afterTime);
					offerDetectTasks(dtoList, taskId);
					JSONUtils.put(statusObject, "" + status, dtoList.size());
				}
				offerNewProxy(taskId);
			} catch (Exception ex) {
				logger.warn(ExceptionUtils.getStackTrace(ex));
			} finally {
				long cost = System.currentTimeMillis() - start;
				String msg = String.format("Detect proxy.taskId:%s,%s,cost:%s", taskId, statusObject.toString(), cost);
				logger.info(msg);
				running = false;
			}

		}

		private void offerNewProxy(String taskId) {
			String type = "ConfigProxyDetector";
			JSONObject argsObject = new JSONObject();
			Date afterTime = new Date();
			afterTime = DateUtils.setHours(afterTime, 0);
			afterTime = DateUtils.setMinutes(afterTime, 0);
			afterTime = DateUtils.setSeconds(afterTime, 0);
			afterTime = DateUtils.setMilliseconds(afterTime, 0);
			List<ProxyAddrDto> dtoList = proxyAddrService.getProxyAddrDtosByCreateTime(afterTime);
			List<TaskPriorityDto> taskDtos = new ArrayList<TaskPriorityDto>(dtoList.size());
			for (ProxyAddrDto dto : dtoList) {
				JSONUtils.put(argsObject, "id", dto.getId());
				JSONUtils.put(argsObject, "ip", dto.getIp());
				JSONUtils.put(argsObject, "port", dto.getPort());
				JSONUtils.put(argsObject, "type", dto.getType());
				JSONUtils.put(argsObject, "url", DEFAULT_DETECT_URL);

				TaskPriorityDto taskDto = new TaskPriorityDto();
				taskDto.setBatchId(taskId);
				taskDto.setType(type);
				taskDto.setLevel(1);
				taskDto.setSource(getName());
				taskDto.setCreatTime(new Date());
				taskDto.setStatus(TaskConstant.TASK_NEW);
				taskDto.setParams(argsObject.toString());
				taskDtos.add(taskDto);
			}
			StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class).addAll(taskDtos);
			logger.info(String.format("add task to buffer,new task size:%d", taskDtos.size()));
		}

		private void offerDetectTasks(List<ProxyDetectDto> dtoList, String taskId) {
			List<TaskPriorityDto> taskDtos = new ArrayList<TaskPriorityDto>();
			JSONObject argsObject = new JSONObject();
			JSONUtils.put(argsObject, "strategy", "ProxyDetectStrategy");
			for (ProxyDetectDto dto : dtoList) {
				JSONUtils.put(argsObject, "id", dto.getId());
				JSONUtils.put(argsObject, "ip", dto.getIp());
				JSONUtils.put(argsObject, "port", dto.getPort());
				JSONUtils.put(argsObject, "type", dto.getType());
				TaskPriorityDto taskDto = new TaskPriorityDto();
				taskDto.setBatchId(taskId);
				taskDto.setType("ConfigProxyDetector");
				// nonuse status,change url to get a chance.
				if (dto.getRetryTimes() > 0) {
					taskDto.setUrl(dto.getUrl());
				} else {
					taskDto.setUrl(DEFAULT_DETECT_URL);
				}
				taskDto.setLevel(1);
				taskDto.setSource("tasker");
				taskDto.setCreatTime(new Date());
				taskDto.setStatus(TaskConstant.TASK_NEW);
				taskDto.setParams(argsObject.toString());
				taskDtos.add(taskDto);
			}
			StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class).addAll(taskDtos);
			logger.info(String.format("add task to buffer,size:%d", taskDtos.size()));
		}

	}

	@Override
	public void close() throws IOException {
		this.timer.cancel();
		this.timer = null;
		logger.info("close " + getName() + " strategy..");
	}
}
