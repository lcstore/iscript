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

import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProxyAddrService;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ProxyDetectStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(ProxyDetectStrategy.class);
	// private static final String DEFAULT_DETECT_URL = "http://www.baidu.com/";
	private static final String DEFAULT_DETECT_URL = "http://www.yhd.com/";
	private static volatile boolean running = false;
	private Timer timer;

	public ProxyDetectStrategy() {
		// ProxyDetectTimer task = new ProxyDetectTimer();
		// this.timer = new Timer("ProxyDetectProducer");
		// this.timer.schedule(task, 1 * 60 * 1000, 100 * 24 * 60 * 60 * 1000);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {

	}


	private class ProxyDetectTimer extends TimerTask {
		private ProxyDetectService proxyDetectService = SpringBeanUtils.getBean(ProxyDetectService.class);
		private ProxyAddrService proxyAddrService = SpringBeanUtils.getBean(ProxyAddrService.class);
		private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);
		private List<Integer> checkStatusList;

		public ProxyDetectTimer() {
			checkStatusList = new ArrayList<Integer>();
			// checkStatusList.add(ProxyDetectDto.STATUS_WORK);
			checkStatusList.add(ProxyDetectDto.STATUS_USABLE);
			checkStatusList.add(ProxyDetectDto.STATUS_RETRY);
//			checkStatusList.add(ProxyDetectDto.STATUS_NONUSE);
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
				int limit = 500;
				for (int status : checkStatusList) {
					int totalCount = 0;
					Long fromId = 0L;
					while (true) {
						List<ProxyDetectDto> dtoList = proxyDetectService.getProxyDetectDtosFromId(fromId, limit,
								status);
						taskId = UUID.randomUUID().toString();
						offerDetectTasks(dtoList, taskId);
						for (ProxyDetectDto dto : dtoList) {
							if (dto.getId() > fromId) {
								fromId = dto.getId();
							}
						}
						totalCount += dtoList.size();
						if (dtoList.size() < limit) {
							break;
						}
					}
					JSONUtils.put(statusObject, "" + status, totalCount);
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
			List<String> urlSet = new ArrayList<String>();
			urlSet.add("http://list.tmall.com/search_product.htm?brand=31840&sort=s&style=w#J_Filter");
			for (ProxyDetectDto dto : dtoList) {
				JSONUtils.put(argsObject, "id", dto.getId());
				JSONUtils.put(argsObject, "ip", dto.getIp());
				JSONUtils.put(argsObject, "port", dto.getPort());
				JSONUtils.put(argsObject, "proxyType", dto.getType());
				TaskPriorityDto taskDto = new TaskPriorityDto();
				taskDto.setBatchId(taskId);
				taskDto.setType("ConfigProxyDetector");
				// sUrl = StringUtils.isEmpty(sUrl) ? DEFAULT_DETECT_URL : sUrl;
				String sUrl = DEFAULT_DETECT_URL;
				taskDto.setUrl(sUrl);
				taskDto.setLevel(1);
				taskDto.setSource("tasker");
				taskDto.setCreatTime(new Date());
				taskDto.setStatus(TaskConstant.TASK_NEW);
				taskDto.setParams(argsObject.toString());
				taskDtos.add(taskDto);
			}
			// StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class).addAll(taskDtos);
			taskPriorityService.batchInsert(taskDtos);
			logger.info(String.format("add task to DB,size:%d", taskDtos.size()));
		}
	}

	@Override
	public void close() throws IOException {
		this.timer.cancel();
		this.timer = null;
		logger.info("close " + getName() + " strategy..");
	}
}
