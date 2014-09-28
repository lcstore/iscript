package com.lezo.iscript.yeam.storage;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.service.ProductService;

public class ProductStorager implements StorageListener<ProductDto> {
	private static Logger logger = LoggerFactory.getLogger(ProductStorager.class);
	private StorageBuffer<ProductDto> storageBuffer;
	@Autowired
	private ProductService productService;

	public ProductStorager() {
		super();
		this.storageBuffer = StorageBufferFactory.getStorageBuffer(ProductDto.class, 100000);
	}

	@Override
	public void doStorage() {
		final List<ProductDto> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			return;
		}
		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// keep sync for the same storager
				long start = System.currentTimeMillis();
				productService.batchSaveProductDtos(copyList);
				long cost = System.currentTimeMillis() - start;
				logger.info(String.format("ProductStorager insert[%s],size:%d,cost:%s", "ProductDto", copyList.size(),
						cost));

			}
		});
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

}
