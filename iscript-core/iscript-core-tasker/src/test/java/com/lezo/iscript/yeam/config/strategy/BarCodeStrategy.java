package com.lezo.iscript.yeam.config.strategy;

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
import com.lezo.iscript.service.crawler.service.BarCodeItemService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.result.storage.StorageCaller;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class BarCodeStrategy implements ResultStrategy, StorageListener<BarCodeItemDto> {
	private static Logger logger = LoggerFactory.getLogger(BarCodeStrategy.class);

	private BarCodeItemService barCodeItemService;

	private StorageBuffer<BarCodeItemDto> storageBuffer;

	@Override
	public String getName() {
		return "BarCodeStrategy";
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_FAIL == rWritable.getStatus()) {

		} else if (ResultWritable.RESULT_SUCCESS == rWritable.getStatus()) {
			addResult(rWritable);
		}

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
		dto.setProductBrand(JSONUtils.getString(rsObject, "品牌"));
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
		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// keep sync for the same storager
				synchronized (this) {
					getBarCodeItemService().batchSaveBarCodeItemDtos(copyList);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public StorageBuffer<BarCodeItemDto> getStorageBuffer() {
		if (this.storageBuffer == null) {
			synchronized (this) {
				if (this.storageBuffer == null) {
					StorageTimeTrigger storageTimeTrigger = SpringBeanUtils.getBean(StorageTimeTrigger.class);
					storageTimeTrigger.addListener(this.getClass(), this);
					this.storageBuffer = (StorageBuffer<BarCodeItemDto>) StorageBufferFactory
							.getStorageBuffer(BarCodeItemDto.class.getName());
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
