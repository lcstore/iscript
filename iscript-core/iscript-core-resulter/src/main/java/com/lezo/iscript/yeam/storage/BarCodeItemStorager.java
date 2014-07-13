package com.lezo.iscript.yeam.storage;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.service.BarCodeItemService;

public class BarCodeItemStorager implements StorageListener<BarCodeItemDto> {
	private static Logger logger = LoggerFactory.getLogger(BarCodeItemStorager.class);
	private static final int capacity = 200;
	private StorageBuffer<BarCodeItemDto> storageBuffer = new StorageBuffer<BarCodeItemDto>(capacity);
	@Autowired
	private BarCodeItemService barCodeItemService;

	@Override
	public void doStorage() {
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

	public StorageBuffer<BarCodeItemDto> getStorageBuffer() {
		return storageBuffer;
	}

	public void setStorageBuffer(StorageBuffer<BarCodeItemDto> storageBuffer) {
		this.storageBuffer = storageBuffer;
	}

	public void setBarCodeItemService(BarCodeItemService barCodeItemService) {
		this.barCodeItemService = barCodeItemService;
	}

}
