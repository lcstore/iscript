package com.lezo.iscript.yeam.config.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.common.storage.StorageTimeTrigger;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.service.BarCodeItemService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
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
		// TODO Auto-generated method stub

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
					handleStorage(copyList);
				}
			}
		});
	}

	protected void handleStorage(List<BarCodeItemDto> copyList) {
		List<BarCodeItemDto> insertDtos = new ArrayList<BarCodeItemDto>();
		List<BarCodeItemDto> updateDtos = new ArrayList<BarCodeItemDto>();
		doAssort(copyList, insertDtos, updateDtos);
		barCodeItemService.batchInsertBarCodeItemDtos(insertDtos);
		barCodeItemService.batchUpdateBarCodeItemDtos(updateDtos);
		logger.info("save BarCodeItemDto.insert:" + insertDtos.size() + ",update:" + updateDtos.size());
	}

	private void doAssort(List<BarCodeItemDto> barCodeItemDtos, List<BarCodeItemDto> insertDtos,
			List<BarCodeItemDto> updateDtos) {
		Map<String, BarCodeItemDto> dtoMap = new HashMap<String, BarCodeItemDto>();
		for (BarCodeItemDto dto : barCodeItemDtos) {
			String key = dto.getBarCode();
			dtoMap.put(key, dto);
		}
		List<String> barCodeList = new ArrayList<String>(dtoMap.keySet());

		List<BarCodeItemDto> hasDtos = barCodeItemService.getBarCodeItemDtos(barCodeList);
		Set<String> hasCodeSet = new HashSet<String>();
		for (BarCodeItemDto oldDto : hasDtos) {
			String key = oldDto.getBarCode();
			hasCodeSet.add(key);
		}
		for (Entry<String, BarCodeItemDto> entry : dtoMap.entrySet()) {
			if (hasCodeSet.contains(entry.getKey())) {
				continue;
			}
			BarCodeItemDto newDto = entry.getValue();
			insertDtos.add(newDto);
		}
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
