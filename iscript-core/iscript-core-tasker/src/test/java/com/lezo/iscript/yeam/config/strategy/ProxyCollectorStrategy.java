package com.lezo.iscript.yeam.config.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.common.storage.StorageTimeTrigger;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.dto.ProxyDetectDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProxyDetectService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.storage.StorageCaller;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ProxyCollectorStrategy implements ResultStrategy, StorageListener<BarCodeItemDto> {
	private static Logger logger = LoggerFactory.getLogger(ProxyCollectorStrategy.class);

	private static final Object SAVE_LOCK = new Object();

	private StorageBuffer<ProxyDetectDto> storageBuffer;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_FAIL == rWritable.getStatus()) {
			addRetry(rWritable);
		} else if (ResultWritable.RESULT_SUCCESS == rWritable.getStatus()) {
			if ("ConfigProxyCollector".equals(rWritable.getType())) {
				JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
				JSONObject argsObject = JSONUtils.get(jObject, "args");
				String rsString = JSONUtils.getString(jObject, "rs");
				List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
				List<ProxyDetectDto> dtoList = new ArrayList<ProxyDetectDto>();
				try {
					JSONObject rootObject = new JSONObject(rsString);
					// addResults(rootObject, argsObject, dtoList);
					addNextTasks(rootObject, argsObject, taskList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!dtoList.isEmpty()) {
					getStorageBuffer().addAll(dtoList);
				}
				if (!taskList.isEmpty()) {
					getTaskPriorityDtoBuffer().addAll(taskList);
				}
			}
		}

	}

	private void addNextTasks(JSONObject rootObject, JSONObject argsObject, List<TaskPriorityDto> dtoList)
			throws Exception {
		JSONArray nextArray = JSONUtils.get(rootObject, "nextList");
		if (nextArray == null) {
			return;
		}

		for (int i = 0; i < nextArray.length(); i++) {
			String nextUrl = nextArray.getString(i);
			TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
			taskPriorityDto.setBatchId(JSONUtils.getString(argsObject, "bid"));
			taskPriorityDto.setType("ConfigProxyCollector");
			taskPriorityDto.setUrl(nextUrl);
			taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
			taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
			taskPriorityDto.setCreatTime(new Date());
			taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
			taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
			argsObject.remove("bid");
			argsObject.remove("type");
			argsObject.remove("url");
			argsObject.remove("level");
			argsObject.remove("src");
			argsObject.remove("ctime");
			if (taskPriorityDto.getLevel() == null) {
				taskPriorityDto.setLevel(0);
			}
			taskPriorityDto.setParams(argsObject.toString());
			dtoList.add(taskPriorityDto);
		}
	}

	private void addResults(JSONObject rootObject, JSONObject argsObject, List<ProxyDetectDto> dtoList)
			throws Exception {
		JSONArray listArray = JSONUtils.get(rootObject, "proxys");
		if (listArray == null) {
			return;
		}

		for (int i = 0; i < listArray.length(); i++) {
			JSONObject newObject = listArray.getJSONObject(i);
			ProxyDetectDto dto = new ProxyDetectDto();
			dto.setIpString(JSONUtils.getString(newObject, "ip"));
			dto.setPort(JSONUtils.getInteger(newObject, "port"));
			dto.setCreateTime(new Date());
			dto.setUpdateTime(dto.getCreateTime());
			dtoList.add(dto);
		}
	}

	@Override
	public void doStorage() {
		StorageBuffer<ProxyDetectDto> storageBuffer = getStorageBuffer();
		final List<ProxyDetectDto> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			return;
		}
		logger.info("start to save dto:" + copyList.size());
		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// keep sync for the same storager
				synchronized (SAVE_LOCK) {
					ProxyDetectService proxyDetectService = SpringBeanUtils.getBean(ProxyDetectService.class);
					proxyDetectService.batchInsertIfAbsent(copyList);
				}
			}
		});
	}

	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return (StorageBuffer<TaskPriorityDto>) StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	public StorageBuffer<ProxyDetectDto> getStorageBuffer() {
		if (this.storageBuffer == null) {
			synchronized (this) {
				if (this.storageBuffer == null) {
					StorageTimeTrigger storageTimeTrigger = SpringBeanUtils.getBean(StorageTimeTrigger.class);
					storageTimeTrigger.addListener(this.getClass(), this);
					this.storageBuffer = StorageBufferFactory.getStorageBuffer(ProxyDetectDto.class);
				}
			}
		}
		return this.storageBuffer;
	}

	private void addRetry(ResultWritable rWritable) {
		JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
		JSONObject argsObject = JSONUtils.get(jObject, "args");
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
		TaskCacher.getInstance().getQueue(rWritable.getType()).offer(tWritable, level);
		logger.warn("retry task:" + tWritable.getId() + ",args:" + new JSONObject(tWritable.getArgs()));
	}
}
