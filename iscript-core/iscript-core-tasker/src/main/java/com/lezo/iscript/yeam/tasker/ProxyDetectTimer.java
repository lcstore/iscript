package com.lezo.iscript.yeam.tasker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.task.TaskConstant;

public class ProxyDetectTimer {
	private static Logger logger = Logger.getLogger(ProxyDetectTimer.class);
	private static volatile boolean running = false;
	@Autowired
	private ProxyDetectService proxyDetectService;
	private List<Integer> checkStatusList;

	public ProxyDetectTimer() {
		checkStatusList = new ArrayList<Integer>();
//		checkStatusList.add(ProxyDetectDto.STATUS_USABLE);
		checkStatusList.add(ProxyDetectDto.STATUS_RETRY);
		checkStatusList.add(ProxyDetectDto.STATUS_NONUSE);
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
		} catch (Exception ex) {
			logger.warn(ExceptionUtils.getStackTrace(ex));
		} finally {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("Detect proxy.taskId:%s,%s,cost:%s", taskId, statusObject.toString(), cost);
			logger.info(msg);
			running = false;
		}

	}

	private void offerDetectTasks(List<ProxyDetectDto> dtoList, String taskId) {
		List<TaskPriorityDto> taskDtos = new ArrayList<TaskPriorityDto>();
		JSONObject argsObject = new JSONObject();
		JSONUtils.put(argsObject, "strategy", "ProxyDetectStrategy");
		for (ProxyDetectDto dto : dtoList) {
			JSONUtils.put(argsObject, "id", dto.getId());
			JSONUtils.put(argsObject, "ip", dto.getIp());
			JSONUtils.put(argsObject, "port", dto.getPort());

			TaskPriorityDto taskDto = new TaskPriorityDto();
			taskDto.setBatchId(taskId);
			taskDto.setType("ConfigProxyDetector");
			// nonuse status,change url to get a chance.
			if (dto.getRetryTimes() > 0) {
				taskDto.setUrl(dto.getUrl());
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

	public void setProxyDetectService(ProxyDetectService proxyDetectService) {
		this.proxyDetectService = proxyDetectService;
	}

}
