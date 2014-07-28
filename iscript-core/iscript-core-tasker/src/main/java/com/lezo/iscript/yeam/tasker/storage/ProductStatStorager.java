package com.lezo.iscript.yeam.tasker.storage;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.yeam.result.storage.StorageCaller;

public class ProductStatStorager implements StorageListener<ProductStatDto> {
	private static Logger logger = LoggerFactory.getLogger(ProductStatStorager.class);
	private StorageBuffer<ProductStatDto> storageBuffer;
	@Autowired
	private ProductStatService productStatService;

	public ProductStatStorager() {
		super();
		this.storageBuffer = StorageBufferFactory.getStorageBuffer(ProductStatDto.class);
	}

	@Override
	public void doStorage() {
		final List<ProductStatDto> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			return;
		}
		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// keep sync for the same storager
				long start = System.currentTimeMillis();
				productStatService.batchSaveProductStatDtos(copyList);
				long cost = System.currentTimeMillis() - start;
				logger.info(String.format("finish to insert[%s],size:%d,cost:%s", this.getClass().getSimpleName(),
						copyList.size(), cost));

			}
		});
	}

	public void setProductStatService(ProductStatService productStatService) {
		this.productStatService = productStatService;
	}

}
