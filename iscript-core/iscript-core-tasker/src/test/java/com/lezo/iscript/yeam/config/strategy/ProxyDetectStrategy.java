package com.lezo.iscript.yeam.config.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ProxyDetectStrategy implements ResultStrategy, StorageListener<BarCodeItemDto> {
	private static Logger logger = LoggerFactory.getLogger(ProxyDetectStrategy.class);

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
			if ("ConfigProxyDetector".equals(rWritable.getType())) {
				JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
				JSONObject argsObject = JSONUtils.get(jObject, "args");
				String rsString = JSONUtils.getString(jObject, "rs");
				List<ProxyDetectDto> dtoList = new ArrayList<ProxyDetectDto>();
				try {
					JSONObject rootObject = new JSONObject(rsString);
					addResults(rootObject, argsObject, dtoList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!dtoList.isEmpty()) {
					getStorageBuffer().addAll(dtoList);
				}
				logger.info("add ProxyDetectDto to buffer,size:" + dtoList.size() + ",total:"
						+ getStorageBuffer().size());
			}
		}

	}

	private void addResults(JSONObject rootObject, JSONObject argsObject, List<ProxyDetectDto> dtoList)
			throws Exception {
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

	@Override
	public void doStorage() {
		logger.info("start ProxyDetectDto...");
		StorageBuffer<ProxyDetectDto> storageBuffer = getStorageBuffer();
		final List<ProxyDetectDto> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			logger.info("insert ProxyDetectDto:0");
			return;
		}

		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// keep sync for the same storager
				synchronized (SAVE_LOCK) {
					ProxyDetectService proxyDetectService = SpringBeanUtils.getBean(ProxyDetectService.class);
					proxyDetectService.batchSaveAfterDetect(copyList);
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
		logger.warn("retry task:" + tWritable.getId() + ",args:" + new JSONObject(tWritable.getArgs()) + ",rs:"
				+ rsObject + ",ex:" + exObject);
		TaskCacher.getInstance().getQueue(rWritable.getType()).offer(tWritable, level);
	}
}
