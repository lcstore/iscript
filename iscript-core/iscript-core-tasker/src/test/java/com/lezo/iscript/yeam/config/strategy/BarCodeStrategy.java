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
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.BarCodeItemService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.storage.StorageCaller;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class BarCodeStrategy implements ResultStrategy, StorageListener<BarCodeItemDto> {
	private static Logger logger = LoggerFactory.getLogger(BarCodeStrategy.class);

	private static final Object SAVE_LOCK = new Object();
	private BarCodeItemService barCodeItemService;

	private StorageBuffer<BarCodeItemDto> storageBuffer;

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_FAIL == rWritable.getStatus()) {
			addRetry(rWritable);
		} else if (ResultWritable.RESULT_SUCCESS == rWritable.getStatus()) {
			if ("Config1688Category".equals(rWritable.getType())) {
				JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
				JSONObject argsObject = JSONUtils.get(jObject, "args");
				String rsString = JSONUtils.getString(jObject, "rs");
				List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
				try {
					JSONArray ctArray = new JSONArray(rsString);
					JSONObject rootObject = new JSONObject();
					JSONUtils.put(rootObject, "children", ctArray);
					addListTasks(rootObject, argsObject, dtoList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!dtoList.isEmpty()) {
					getTaskPriorityDtoBuffer().addAll(dtoList);
				}
			} else if ("Config1688List".equals(rWritable.getType())) {
				JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
				JSONObject argsObject = JSONUtils.get(jObject, "args");
				String rsString = JSONUtils.getString(jObject, "rs");
				List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
				try {
					JSONObject rootObject = new JSONObject(rsString);
					addProductTasks(rootObject, argsObject, dtoList);
					addNextListTasks(rootObject, argsObject, dtoList);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!dtoList.isEmpty()) {
					getTaskPriorityDtoBuffer().addAll(dtoList);
				}
			} else if ("Config1688Product".equals(rWritable.getType())) {
				addResult(rWritable);
			}
		}

	}

	private void addNextListTasks(JSONObject rootObject, JSONObject argsObject, List<TaskPriorityDto> dtoList) throws Exception {
		JSONArray nextArray = JSONUtils.get(rootObject, "nextList");
		if (nextArray == null) {
			return;
		}

		for (int i = 0; i < nextArray.length(); i++) {
			String nextUrl = nextArray.getString(i);
			TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
			taskPriorityDto.setBatchId(JSONUtils.getString(argsObject, "bid"));
			taskPriorityDto.setType("Config1688List");
			taskPriorityDto.setUrl(nextUrl);
			taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
			taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
			taskPriorityDto.setCreatTime(new Date());
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

	private void addProductTasks(JSONObject rootObject, JSONObject argsObject, List<TaskPriorityDto> dtoList) throws Exception {
		JSONArray listArray = JSONUtils.get(rootObject, "list");
		if (listArray == null) {
			return;
		}

		for (int i = 0; i < listArray.length(); i++) {
			JSONObject newObject = listArray.getJSONObject(i);
			TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
			taskPriorityDto.setBatchId(JSONUtils.getString(argsObject, "bid"));
			taskPriorityDto.setType("Config1688Product");
			taskPriorityDto.setUrl(JSONUtils.getString(newObject, "url"));
			taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
			taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
			taskPriorityDto.setCreatTime(new Date());
			taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
			argsObject.remove("bid");
			argsObject.remove("type");
			argsObject.remove("url");
			argsObject.remove("level");
			argsObject.remove("src");
			argsObject.remove("ctime");
			JSONUtils.put(argsObject, "fromUrl", JSONUtils.getString(argsObject, "url"));
			JSONUtils.put(argsObject, "pname", JSONUtils.getString(newObject, "name"));
			taskPriorityDto.setParams(argsObject.toString());

			if (taskPriorityDto.getLevel() == null) {
				taskPriorityDto.setLevel(0);
			}
			dtoList.add(taskPriorityDto);
		}
	}

	private void addListTasks(JSONObject ctObject, JSONObject argsObject, List<TaskPriorityDto> dtoList) throws Exception {
		JSONArray childArray = JSONUtils.get(ctObject, "children");
		if (childArray == null || childArray.length() < 1) {
			TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
			taskPriorityDto.setBatchId(JSONUtils.getString(argsObject, "bid"));
			taskPriorityDto.setType("Config1688List");
			taskPriorityDto.setUrl(JSONUtils.getString(ctObject, "url"));
			taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
			taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
			taskPriorityDto.setCreatTime(new Date());
			taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
			argsObject.remove("bid");
			argsObject.remove("type");
			argsObject.remove("url");
			argsObject.remove("level");
			argsObject.remove("src");
			argsObject.remove("ctime");
			JSONUtils.put(argsObject, "cname", JSONUtils.getString(ctObject, "name"));
			taskPriorityDto.setParams(argsObject.toString());
			if (taskPriorityDto.getLevel() == null) {
				taskPriorityDto.setLevel(0);
			}
			dtoList.add(taskPriorityDto);
		} else {
			for (int i = 0; i < childArray.length(); i++) {
				JSONObject newObject = childArray.getJSONObject(i);
				addListTasks(newObject, argsObject, dtoList);
			}
		}
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

	private void addResult(ResultWritable rWritable) {
		JSONObject jObject = JSONUtils.getJSONObject(rWritable.getResult());
		JSONObject argsObject = JSONUtils.get(jObject, "args");
		String rsString = JSONUtils.getString(jObject, "rs");
		JSONObject rsObject = JSONUtils.getJSONObject(rsString);

		BarCodeItemDto dto = new BarCodeItemDto();
		dto.setProductUrl(JSONUtils.getString(argsObject, "url"));
		dto.setBarCode(JSONUtils.getString(rsObject, "barCode"));
		dto.setProductName(JSONUtils.getString(rsObject, "name"));
		dto.setProductBrand(JSONUtils.getString(rsObject, "brand"));
		dto.setImgUrl(JSONUtils.getString(rsObject, "imgUrl"));
		rsObject.remove("barCode");
		rsObject.remove("name");
		rsObject.remove("brand");
		rsObject.remove("imgUrl");
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		dto.setProductAttr(rsObject.toString());
		if (BarCodeUtils.isBarCode(dto.getBarCode())) {
			getStorageBuffer().add(dto);
		} else {
			logger.warn("illegal barcode:" + dto.getBarCode() + ",url:" + dto.getProductUrl());
		}
	}

	@Override
	public void doStorage() {
		StorageBuffer<BarCodeItemDto> storageBuffer = getStorageBuffer();
		final List<BarCodeItemDto> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			return;
		}
		logger.info("start to save dto:" + copyList.size());
		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// keep sync for the same storager
				synchronized (SAVE_LOCK) {
					getBarCodeItemService().batchSaveBarCodeItemDtos(copyList);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return (StorageBuffer<TaskPriorityDto>) StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	public StorageBuffer<BarCodeItemDto> getStorageBuffer() {
		if (this.storageBuffer == null) {
			synchronized (this) {
				if (this.storageBuffer == null) {
					StorageTimeTrigger storageTimeTrigger = SpringBeanUtils.getBean(StorageTimeTrigger.class);
					storageTimeTrigger.addListener(this.getClass(), this);
					this.storageBuffer = (StorageBuffer<BarCodeItemDto>) StorageBufferFactory.getStorageBuffer(BarCodeItemDto.class);
				}
			}
		}
		return this.storageBuffer;
	}

	public BarCodeItemService getBarCodeItemService() {
		if (barCodeItemService == null) {
			barCodeItemService = SpringBeanUtils.getBean(BarCodeItemService.class);
		}
		return barCodeItemService;
	}

}
