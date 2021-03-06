package com.lezo.iscript.yeam.storage;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.service.crawler.service.SessionHisService;

public class SessionStorager implements StorageListener<SessionHisDto> {
	private static Logger logger = LoggerFactory.getLogger(SessionStorager.class);
	private StorageBuffer<SessionHisDto> storageBuffer;
	@Autowired
	private SessionHisService sessionHisService;

	public SessionStorager() {
		super();
		this.storageBuffer = StorageBufferFactory.getStorageBuffer(SessionHisDto.class);
	}

	@Override
	public void doStorage() {
		final List<SessionHisDto> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			logger.info("SessionStorager task is empty...");
			return;
		}
		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// keep sync for the same storager
				long start = System.currentTimeMillis();
				sessionHisService.batchSaveSessionHisDtos(copyList);
				long cost = System.currentTimeMillis() - start;
				logger.info(String.format("SessionStorager insert[%s],size:%d,cost:%s", "SessionHisDto",
						copyList.size(), cost));
			}
		});
	}

	public void setSessionHisService(SessionHisService sessionHisService) {
		this.sessionHisService = sessionHisService;
	}

}
