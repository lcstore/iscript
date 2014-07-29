package com.lezo.iscript.yeam.tasker.storage;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.common.storage.StorageListener;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.yeam.result.storage.StorageCaller;

public class TaskPriorityStorager implements StorageListener<TaskPriorityDto> {
	private static Logger logger = LoggerFactory.getLogger(TaskPriorityStorager.class);
	private StorageBuffer<TaskPriorityDto> storageBuffer;
	@Autowired
	private TaskPriorityService taskPriorityService;

	public TaskPriorityStorager() {
		super();
		this.storageBuffer = StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	@Override
	public void doStorage() {
		final List<TaskPriorityDto> copyList = storageBuffer.moveTo();
		if (CollectionUtils.isEmpty(copyList)) {
			return;
		}
		StorageCaller.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// keep sync for the same storager
				long start = System.currentTimeMillis();
				taskPriorityService.batchInsert(copyList);
				long cost = System.currentTimeMillis() - start;
				logger.info(String.format("TaskPriorityStorager insert[%s],size:%d,cost:%s", "TaskPriorityDto",
						copyList.size(), cost));

			}
		});
	}

	public void setTaskPriorityService(TaskPriorityService taskPriorityService) {
		this.taskPriorityService = taskPriorityService;
	}

}
