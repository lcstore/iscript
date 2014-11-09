package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class DailyUpdateStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(DailyUpdateStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;
	private CreateTaskTimer task = new CreateTaskTimer();

	public DailyUpdateStrategy() {
		this.timer = new Timer("CreateTaskTimer");
		this.timer.schedule(task, 60 * 1000, 8 * 60 * 60 * 1000);
	}

	public Date getSignDate(int addDay, int hour, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.add(Calendar.DAY_OF_MONTH, addDay);
		return c.getTime();
	}

	private class CreateTaskTimer extends TimerTask {
		private Map<Integer, String> typeMap;

		public CreateTaskTimer() {
			typeMap = new HashMap<Integer, String>();
			typeMap.put(1001, "ConfigJdProduct");
			typeMap.put(1002, "ConfigYhdProduct");
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
				ProductService productService = SpringBeanUtils.getBean(ProductService.class);
				for (Entry<Integer, String> entry : typeMap.entrySet()) {
					Integer siteId = entry.getKey();
					String type = entry.getValue();
					int limit = 500;
					Long fromId = 0L;
					long startType = System.currentTimeMillis();
					while (true) {
						List<ProductDto> dtoList = productService.getProductDtosFromId(fromId, limit, siteId);
						offerTasks(type, dtoList, fromId);
						if (dtoList.size() < limit) {
							break;
						} else {
							fromId = getMaxId(dtoList);
						}
					}
					long cost = System.currentTimeMillis() - startType;
					logger.info("Offer task:{},cost:{}", type, cost);
				}
			} catch (Exception ex) {
				logger.warn(ExceptionUtils.getStackTrace(ex));
			} finally {
				long cost = System.currentTimeMillis() - start;
				logger.info("CreateTaskTimer is done.cost:{}", cost);
				running = false;
			}
		}

		private void offerTasks(String type, List<ProductDto> dtoList, Long fromId) {
			JSONObject paramObject = new JSONObject();
			JSONUtils.put(paramObject, "bid", fromId);
			List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(dtoList.size());
			for (ProductDto dto : dtoList) {
				if (StringUtils.isEmpty(dto.getProductUrl())) {
					logger.warn("id:{},siteId:{},pCode:{},url is empty.", dto.getId(), dto.getSiteId(), dto.getProductCode());
					continue;
				}
				TaskPriorityDto taskDto = createPriorityDto(dto.getProductUrl(), type, paramObject);
				taskList.add(taskDto);

			}
			logger.info("Offer new task.type:{},size:{}", type, taskList.size());
			getTaskPriorityDtoBuffer().addAll(taskList);
		}

		private Long getMaxId(List<ProductDto> dtoList) {
			Long maxId = 0L;
			for (ProductDto dto : dtoList) {
				if (dto.getId() > maxId) {
					maxId = dto.getId();
				}
			}
			return maxId;
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
			logger.info(rWritable.getResult());
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