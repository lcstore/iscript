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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.ProxyAddrDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProxyAddrService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ProxyCheckStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(ProxyCheckStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;

	public ProxyCheckStrategy() {
		ProxyDetectTimer task = new ProxyDetectTimer();
		this.timer = new Timer(this.getName());
		this.timer.schedule(task, 1 * 60 * 1000, 12 * 60 * 60 * 1000);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {

	}

	private class ProxyDetectTimer extends TimerTask {
		private ProxyAddrService proxyAddrService = SpringBeanUtils.getBean(ProxyAddrService.class);

		public ProxyDetectTimer() {
		}

		public void run() {
			if (running) {
				logger.warn(getName() + " taskTimer is working...");
				return;
			}
			long start = System.currentTimeMillis();
			String taskId = UUID.randomUUID().toString();
			int size = 0;
			try {
				logger.info(getName() + " taskTimer is start...");
				List<ProxyAddrDto> dtoList = proxyAddrService.getNullRegionProxyAddrDtos(0);
				offerDetectTasks(dtoList, taskId);
				size = dtoList.size();
				running = true;
			} catch (Exception ex) {
				logger.warn(ExceptionUtils.getStackTrace(ex));
			} finally {
				long cost = System.currentTimeMillis() - start;
				logger.info(getName() + " taskTimer is done.taskId:" + taskId + ",size:" + size + ",cost:" + cost);
				running = false;
			}

		}

		private void offerDetectTasks(List<ProxyAddrDto> dtoList, String taskId) {
			List<TaskPriorityDto> taskDtos = new ArrayList<TaskPriorityDto>();
			JSONObject argsObject = new JSONObject();
			String type = "ConfigProxyChecker";
			JSONUtils.put(argsObject, "strategy", getName());
			JSONUtils.put(argsObject, "retry", 0);
			for (ProxyAddrDto dto : dtoList) {
				JSONUtils.put(argsObject, "id", dto.getId());
				JSONUtils.put(argsObject, "ip", dto.getIp());
				JSONUtils.put(argsObject, "port", dto.getPort());

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
			logger.info(String.format("add task[%s] to buffer,size:%d", type, taskDtos.size()));
		}

	}

	@Override
	public void close() throws IOException {
		this.timer.cancel();
		this.timer = null;
		logger.info("close " + getName() + " strategy..");
	}
}